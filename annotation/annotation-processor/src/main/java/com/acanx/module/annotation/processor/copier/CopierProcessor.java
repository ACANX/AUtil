package com.acanx.module.annotation.processor.copier;

import com.acanx.util.incubator.annotation.Copier;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.Getter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copier 注解处理器
 * 用于在编译期生成对象拷贝代码
 */
@SupportedAnnotationTypes("com.acanx.util.incubator.annotation.Copier")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class CopierProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 按类分组，同一个类的所有@Copier 方法生成在同一个辅助类中
        Map<TypeElement, List<ExecutableElement>> methodsByClass = new LinkedHashMap<>();
        // 记录使用 BYTECODE 模式的方法（这些方法需要 AST 处理器处理）
        List<ExecutableElement> bytecodeModeMethods = new ArrayList<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Copier.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    "@Copier 只能用于方法", element);
                continue;
            }

            ExecutableElement method = (ExecutableElement) element;
            Copier copier = method.getAnnotation(Copier.class);
            
            // 检查生成模式
            if (copier.generationMode() == Copier.GenerationMode.BYTECODE) {
                // BYTECODE 模式需要 AST 处理器处理，这里跳过
                bytecodeModeMethods.add(method);
                continue;
            }
            
            TypeElement enclosingClass = (TypeElement) method.getEnclosingElement();
            methodsByClass.computeIfAbsent(enclosingClass, k -> new ArrayList<>()).add(method);
        }

        // 提示 BYTECODE 模式的方法
        if (!bytecodeModeMethods.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE,
                "发现 " + bytecodeModeMethods.size() + " 个方法使用 BYTECODE 模式，" +
                "这些方法需要 annotation-processor-ast 模块处理。" +
                "如果未配置 AST 处理器，这些方法将不会被增强。");
        }

        // 为每个类生成一个辅助类（仅 ASM 模式）
        for (Map.Entry<TypeElement, List<ExecutableElement>> entry : methodsByClass.entrySet()) {
            TypeElement enclosingClass = entry.getKey();
            List<ExecutableElement> methods = entry.getValue();

            try {
                generateHelperClassForMethods(enclosingClass, methods);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                    "生成代码时出错：" + e.getMessage(), methods.get(0));
            }
        }

        return true;
    }

    private void generateHelperClassForMethods(TypeElement enclosingClass, 
                                                List<ExecutableElement> methods) throws IOException {
        // 生成类名：原类名 + Copier（驼峰命名，无下划线）
        // 例如：Test + Copier = TestCopier
        String helperClassName = enclosingClass.getSimpleName().toString() + "Copier";

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(helperClassName)
            .addModifiers(Modifier.PUBLIC);

        // 为每个@Copier 方法生成一个静态方法
        for (ExecutableElement method : methods) {
            List<? extends VariableElement> parameters = method.getParameters();

            if (parameters.size() < 2) {
                messager.printMessage(Diagnostic.Kind.ERROR, 
                    "@Copier 注解的方法必须至少有两个参数 (src, dest)", method);
                continue;
            }

            VariableElement srcParam = parameters.get(0);
            VariableElement destParam = parameters.get(1);

            Copier copier = method.getAnnotation(Copier.class);
            TypeMirror srcType = srcParam.asType();
            TypeMirror destType = destParam.asType();

            // 获取源类和目标类的字段
            Map<String, FieldInfo> srcFields = getAllFields(srcType);
            Map<String, FieldInfo> destFields = getAllFields(destType);

            // 构建方法体
            List<CodeBlock> statements = new ArrayList<>();

            // 空值检查
            statements.add(CodeBlock.of("if ($L == null || $L == null) return;", 
                srcParam.getSimpleName(), destParam.getSimpleName()));

            // 找出共同的字段
            Set<String> commonFields = new HashSet<>(srcFields.keySet());
            commonFields.retainAll(destFields.keySet());

            // 处理 include/exclude
            Set<String> fieldsToCopy = filterFields(commonFields, copier);

            // 为每个字段生成拷贝代码
            for (String fieldName : fieldsToCopy) {
                FieldInfo srcField = srcFields.get(fieldName);
                FieldInfo destField = destFields.get(fieldName);

                if (srcField == null || destField == null) {
                    continue;
                }

                // 检查类型是否兼容
                if (!isTypeCompatible(srcField.type, destField.type)) {
                    messager.printMessage(Diagnostic.Kind.WARNING, 
                        "字段类型不兼容：" + fieldName + 
                        " src=" + srcField.type + " dest=" + destField.type, method);
                    continue;
                }

                generateFieldCopy(statements, srcParam, destParam, fieldName, 
                    srcField, destField, copier, 0);
            }

            // 添加方法到类中，方法名沿用原来的方法名
            String methodName = method.getSimpleName().toString();
            classBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeName.get(srcParam.asType()),
                    srcParam.getSimpleName().toString())
                .addParameter(TypeName.get(destParam.asType()),
                    destParam.getSimpleName().toString())
                .addCode(CodeBlock.join(statements, "\n"))
                .build());
        }

        JavaFile.builder(
            getPackageName(enclosingClass),
            classBuilder.build())
            .build()
            .writeTo(filer);
    }

    private void generateFieldCopy(List<CodeBlock> statements, 
                                   VariableElement srcParam, 
                                   VariableElement destParam,
                                   String fieldName,
                                   FieldInfo srcField,
                                   FieldInfo destField,
                                   Copier copier,
                                   int depth) {
        
        boolean useAccessors = copier.useAccessors();
        boolean ignoreNull = copier.ignoreNull();
        boolean deepCopy = copier.strategy() == Copier.CopyStrategy.DEEP;

        String srcGetter = useAccessors ? 
            "get" + capitalize(fieldName) : fieldName;
        String destSetter = useAccessors ? 
            "set" + capitalize(fieldName) : fieldName;
        String destGetter = useAccessors ? 
            "get" + capitalize(fieldName) : fieldName;
        
        // 访问器后缀：getter 需要 ()，setter 不需要（因为后面会有参数）
        String srcAccessorSuffix = "()";
        String destAccessorSuffix = useAccessors ? "" : "";  // setter 方法名本身，不需要后缀

        TypeMirror srcFieldType = srcField.type;
        TypeMirror destFieldType = destField.type;

        // 基本类型和 String 直接拷贝
        if (isPrimitiveOrString(srcFieldType)) {
            if (ignoreNull) {
                statements.add(CodeBlock.of(
                    "if ($L.$L$L != null) $L.$L($L.$L$L);",
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                    destParam.getSimpleName(), destSetter,
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
            } else {
                statements.add(CodeBlock.of(
                    "$L.$L($L.$L$L);",
                    destParam.getSimpleName(), destSetter,
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
            }
            return;
        }

        // 处理集合类型
        if (isCollection(srcFieldType)) {
            generateCollectionCopy(statements, srcParam, destParam, fieldName,
                srcField, destField, copier, useAccessors, ignoreNull, srcAccessorSuffix);
            return;
        }

        // 处理数组类型
        if (srcFieldType.getKind() == TypeKind.ARRAY) {
            generateArrayCopy(statements, srcParam, destParam, fieldName,
                srcField, destField, copier, useAccessors, ignoreNull, srcAccessorSuffix);
            return;
        }

        // 处理复杂对象 - 深拷贝或浅拷贝
        if (deepCopy && isComplexType(srcFieldType)) {
            // 深拷贝：创建新对象并拷贝字段
            if (ignoreNull) {
                statements.add(CodeBlock.of(
                    "if ($L.$L$L != null) {\n" +
                    "    $L.$L(new $T());\n" +
                    "    // 递归拷贝 $L 内部字段\n" +
                    "    $L.$L$L.$L($L.$L$L.$L());\n" +
                    "}",
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                    destParam.getSimpleName(), destSetter,
                    TypeName.get(destFieldType),
                    fieldName,
                    destParam.getSimpleName(), destGetter, srcAccessorSuffix,
                    "copyTo",
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                    "copyTo"));
            } else {
                statements.add(CodeBlock.of(
                    "$L.$L(new $T());\n" +
                    "// 递归拷贝 $L 内部字段\n" +
                    "$L.$L$L.$L($L.$L$L.$L());",
                    destParam.getSimpleName(), destSetter,
                    TypeName.get(destFieldType),
                    fieldName,
                    destParam.getSimpleName(), destGetter, srcAccessorSuffix,
                    "copyTo",
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                    "copyTo"));
            }
        } else {
            // 浅拷贝：直接赋值
            if (ignoreNull) {
                statements.add(CodeBlock.of(
                    "if ($L.$L$L != null) $L.$L($L.$L$L);",
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                    destParam.getSimpleName(), destSetter,
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
            } else {
                statements.add(CodeBlock.of(
                    "$L.$L($L.$L$L);",
                    destParam.getSimpleName(), destSetter,
                    srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
            }
        }
    }

    private void generateCollectionCopy(List<CodeBlock> statements,
                                        VariableElement srcParam,
                                        VariableElement destParam,
                                        String fieldName,
                                        FieldInfo srcField,
                                        FieldInfo destField,
                                        Copier copier,
                                        boolean useAccessors,
                                        boolean ignoreNull,
                                        String srcAccessorSuffix) {

        String srcGetter = useAccessors ? "get" + capitalize(fieldName) : fieldName;
        String destSetter = useAccessors ? "set" + capitalize(fieldName) : fieldName;

        // 使用 ArrayList 来实例化集合
        String destTypeName = destField.type.toString();
        String implClass = "java.util.ArrayList";
        if (destTypeName.contains("Set")) {
            implClass = "java.util.HashSet";
        } else if (destTypeName.contains("Map")) {
            implClass = "java.util.HashMap";
        }

        if (ignoreNull) {
            statements.add(CodeBlock.of(
                "if ($L.$L$L != null) $L.$L(new $T($L.$L$L));",
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                destParam.getSimpleName(), destSetter,
                ClassName.get("java.util", implClass.substring(implClass.lastIndexOf('.') + 1)),
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
        } else {
            statements.add(CodeBlock.of(
                "$L.$L($L.$L$L == null ? null : new $T($L.$L$L));",
                destParam.getSimpleName(), destSetter,
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                ClassName.get("java.util", implClass.substring(implClass.lastIndexOf('.') + 1)),
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
        }
    }

    private void generateArrayCopy(List<CodeBlock> statements,
                                   VariableElement srcParam,
                                   VariableElement destParam,
                                   String fieldName,
                                   FieldInfo srcField,
                                   FieldInfo destField,
                                   Copier copier,
                                   boolean useAccessors,
                                   boolean ignoreNull,
                                   String srcAccessorSuffix) {

        String srcGetter = useAccessors ? "get" + capitalize(fieldName) : fieldName;
        String destSetter = useAccessors ? "set" + capitalize(fieldName) : fieldName;

        if (ignoreNull) {
            statements.add(CodeBlock.of(
                "if ($L.$L$L != null) $L.$L($L.$L$L.clone());",
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                destParam.getSimpleName(), destSetter,
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
        } else {
            statements.add(CodeBlock.of(
                "$L.$L($L.$L$L == null ? null : $L.$L$L.clone());",
                destParam.getSimpleName(), destSetter,
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix,
                srcParam.getSimpleName(), srcGetter, srcAccessorSuffix));
        }
    }

    private Map<String, FieldInfo> getAllFields(TypeMirror type) {
        Map<String, FieldInfo> fields = new LinkedHashMap<>();
        collectFields(type, fields, new HashSet<>());
        return fields;
    }

    private void collectFields(TypeMirror type, Map<String, FieldInfo> fields, Set<TypeMirror> visited) {
        if (visited.contains(type)) {
            return;
        }
        visited.add(type);

        if (!(type instanceof DeclaredType)) {
            return;
        }

        DeclaredType declaredType = (DeclaredType) type;
        TypeElement element = (TypeElement) declaredType.asElement();

        // 获取父类的字段
        TypeMirror superclass = element.getSuperclass();
        if (superclass != null && superclass.getKind() != TypeKind.NONE) {
            collectFields(superclass, fields, visited);
        }

        // 获取当前类的字段
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosed;
                // 跳过 static 和 transient 字段
                Set<Modifier> modifiers = field.getModifiers();
                if (modifiers.contains(Modifier.STATIC) || 
                    modifiers.contains(Modifier.TRANSIENT)) {
                    continue;
                }
                fields.put(field.getSimpleName().toString(), 
                    new FieldInfo(field.getSimpleName().toString(), 
                        field.asType(), field));
            }
        }
    }

    private Set<String> filterFields(Set<String> fields, Copier copier) {
        Set<String> result = new HashSet<>(fields);

        // 处理 include
        String[] include = copier.include();
        if (include.length > 0) {
            result.retainAll(Arrays.stream(include).collect(Collectors.toSet()));
        }

        // 处理 exclude
        String[] exclude = copier.exclude();
        if (exclude.length > 0) {
            Arrays.stream(exclude).forEach(result::remove);
        }

        return result;
    }

    private boolean isPrimitiveOrString(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return true;
        }
        String typeName = type.toString();
        return "java.lang.String".equals(typeName) ||
               "java.lang.Boolean".equals(typeName) ||
               "java.lang.Character".equals(typeName) ||
               "java.lang.Byte".equals(typeName) ||
               "java.lang.Short".equals(typeName) ||
               "java.lang.Integer".equals(typeName) ||
               "java.lang.Long".equals(typeName) ||
               "java.lang.Float".equals(typeName) ||
               "java.lang.Double".equals(typeName);
    }

    private boolean isCollection(TypeMirror type) {
        String typeName = type.toString();
        return typeName.startsWith("java.util.List") ||
               typeName.startsWith("java.util.Set") ||
               typeName.startsWith("java.util.Map") ||
               typeName.startsWith("java.util.Collection");
    }

    private boolean isComplexType(TypeMirror type) {
        return !isPrimitiveOrString(type) && !isCollection(type) && 
               type.getKind() != TypeKind.ARRAY;
    }

    private boolean isTypeCompatible(TypeMirror srcType, TypeMirror destType) {
        // 相同类型
        if (typeUtils.isSameType(srcType, destType)) {
            return true;
        }
        // 源类型是目标类型的子类型
        if (typeUtils.isSubtype(srcType, destType)) {
            return true;
        }
        // 基本类型和包装类型兼容
        if (srcType.getKind().isPrimitive() || destType.getKind().isPrimitive()) {
            return true;
        }
        return false;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Getter
    private static class FieldInfo {
        private final String name;
        private final TypeMirror type;
        private final VariableElement element;

        FieldInfo(String name, TypeMirror type, VariableElement element) {
            this.name = name;
            this.type = type;
            this.element = element;
        }
    }
}
