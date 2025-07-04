package com.acanx.util.incubator.annotation;

import com.acanx.annotation.ObjectCopy;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.util.Types;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * ObjectCopyProcessor
 *
 * @author ACANX
 * @since 202506
 */
@SupportedAnnotationTypes("com.acanx.annotation.ObjectCopy")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedOptions({"incremental"}) // 支持增量编译
public class ObjectCopyProcessor extends AbstractProcessor {

    public static final String SUFFIX = "Copier";
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    // 全局跟踪已生成的文件
    private static final Set<String> generatedFiles = new HashSet<>();

    // 收集使用说明
    private static final Map<String, List<String>> usageInstructions = new HashMap<>();

    // 基本类型与包装类型映射
    private static final Map<String, String> primitiveToWrapper = Map.of(
            "int", "java.lang.Integer",
            "long", "java.lang.Long",
            "double", "java.lang.Double",
            "float", "java.lang.Float",
            "boolean", "java.lang.Boolean",
            "char", "java.lang.Character",
            "byte", "java.lang.Byte",
            "short", "java.lang.Short"
    );

    // 兼容类型映射
    private static final Map<String, Set<String>> compatibleTypes = Map.of(
            "java.lang.String", Set.of("java.lang.CharSequence"),
            "java.lang.CharSequence", Set.of("java.lang.String"),
            "java.util.Date", Set.of("java.time.Instant", "java.time.LocalDateTime", "java.time.ZonedDateTime"),
            "java.time.Instant", Set.of("java.util.Date", "java.time.LocalDateTime", "java.time.ZonedDateTime"),
            "java.time.LocalDateTime", Set.of("java.util.Date", "java.time.Instant", "java.time.ZonedDateTime"),
            "java.time.ZonedDateTime", Set.of("java.util.Date", "java.time.Instant", "java.time.LocalDateTime")
    );

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                printUsageInstructions();
                return false;
            }

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ObjectCopy.class);
            List<ExecutableElement> methods = elements.stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .map(e -> (ExecutableElement) e)
                    .collect(Collectors.toList());

            if (methods.isEmpty()) return false;

            Map<TypeElement, List<ExecutableElement>> methodsByClass = methods.stream()
                    .collect(Collectors.groupingBy(
                            method -> (TypeElement) method.getEnclosingElement()
                    ));

            for (Map.Entry<TypeElement, List<ExecutableElement>> entry : methodsByClass.entrySet()) {
                TypeElement enclosingClass = entry.getKey();
                List<ExecutableElement> classMethods = entry.getValue();
                String helperClassName = enclosingClass.getSimpleName().toString() + SUFFIX;

                createCopierClass(enclosingClass, classMethods, helperClassName);
                collectUsageInstructions(enclosingClass, classMethods, helperClassName);
            }

            return true;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "处理器异常: " + e.getMessage());
            return false;
        }
    }

    private void createCopierClass(TypeElement enclosingClass,
                                   List<ExecutableElement> methods,
                                   String helperClassName) {
        try {
            String packageName = elementUtils.getPackageOf(enclosingClass).toString();
            String fullClassName = packageName + "." + helperClassName;

            if (generatedFiles.contains(fullClassName)) return;

            TypeSpec.Builder helperClassBuilder = TypeSpec.classBuilder(helperClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc("Incubator-Annotation生成的对象拷贝Copier类\n\n")
                    .addJavadoc("支持功能:\n")
                    .addJavadoc("- 同名同类型字段自动复制\n")
                    .addJavadoc("- 基本类型与包装类型兼容\n")
                    .addJavadoc("- 常用类型自动转换\n")
                    .addJavadoc("- 字段映射配置\n\n")
                    .addJavadoc("方法列表:\n");

            for (ExecutableElement method : methods) {
                String methodName = method.getSimpleName().toString();
                String helperMethodName = methodName;
                ObjectCopy annotation = method.getAnnotation(ObjectCopy.class);

                helperClassBuilder.addJavadoc("- $L()\n", helperMethodName);

                MethodSpec helperMethod = generateCopyMethod(method, helperMethodName, annotation);
                if (helperMethod != null) {
                    helperClassBuilder.addMethod(helperMethod);
                }
            }

            JavaFile javaFile = JavaFile.builder(packageName, helperClassBuilder.build())
                    .indent("  ")
                    .build();

            javaFile.writeTo(filer);
            generatedFiles.add(fullClassName);

        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "创建Copier类失败: " + e.getMessage(), enclosingClass);
        }
    }

    private MethodSpec generateCopyMethod(ExecutableElement method,
                                          String helperMethodName,
                                          ObjectCopy annotation) {
        List<? extends VariableElement> parameters = method.getParameters();

        if (parameters.size() != 2) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ObjectCopy 方法必须有两个参数", method);
            return null;
        }

        VariableElement sourceParam = parameters.get(0);
        VariableElement targetParam = parameters.get(1);
        TypeElement sourceType = (TypeElement) typeUtils.asElement(sourceParam.asType());
        TypeElement targetType = (TypeElement) typeUtils.asElement(targetParam.asType());

        if (sourceType == null || targetType == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "无法解析源或目标类型", method);
            return null;
        }

        // 添加方法Javadoc
        CodeBlock.Builder javadocBuilder = CodeBlock.builder();
        javadocBuilder.add("Incubator-Annotation生成的拷贝方法\n用于替换 {@link $T#$L($T, $T)}\n\n",
                method.getEnclosingElement(),
                method.getSimpleName().toString(),
                sourceParam.asType(),
                targetParam.asType());

        javadocBuilder.add("配置信息:\n");
        javadocBuilder.add("- copyNulls: $L\n", annotation.copyNulls());
        javadocBuilder.add("- 忽略字段: $L\n", Arrays.toString(annotation.ignoreFields()));

        if (annotation.fieldMappings().length > 0) {
            javadocBuilder.add("- 字段映射:\n");
            for (ObjectCopy.FieldMapping mapping : annotation.fieldMappings()) {
                javadocBuilder.add("  - $L → $L\n", mapping.s(), mapping.t());
            }
        }

        // 添加参数文档
        javadocBuilder.add("\n@param source 源对象，包含待拷贝的数据\n");
        javadocBuilder.add("@param target 目标对象，接收拷贝后的数据\n");

        MethodSpec.Builder builder = MethodSpec.methodBuilder(helperMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addJavadoc(javadocBuilder.build())
                .addParameter(TypeName.get(sourceParam.asType()), "source")
                .addParameter(TypeName.get(targetParam.asType()), "target");

        addFieldCopyStatements(builder, annotation, sourceType, targetType);
        return builder.build();
    }

    private void addFieldCopyStatements(MethodSpec.Builder builder,
                                        ObjectCopy config,
                                        TypeElement sourceTypeElement,
                                        TypeElement targetTypeElement) {

        boolean copyNulls = config.copyNulls();
        Set<String> ignoredFields = new HashSet<>(Arrays.asList(config.ignoreFields()));

        // 1. 准备字段映射
        Map<String, String> fieldMappings = new HashMap<>();
        for (ObjectCopy.FieldMapping mapping : config.fieldMappings()) {
            fieldMappings.put(mapping.s(), mapping.t());
        }

        // 2. 收集源类字段
        Map<String, VariableElement> sourceFields = new HashMap<>();
        for (Element element : sourceTypeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) element;
                sourceFields.put(field.getSimpleName().toString(), field);
            }
        }

        // 3. 收集目标类字段
        Map<String, VariableElement> targetFields = new HashMap<>();
        for (Element element : targetTypeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) element;
                targetFields.put(field.getSimpleName().toString(), field);
            }
        }

        // 4. 使用代码块构建拷贝逻辑
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.add("if (null != source && null != target) {\n");

        // 5. 处理自动映射的同名字段
        for (String sourceFieldName : sourceFields.keySet()) {
            if (ignoredFields.contains(sourceFieldName)) continue;
            if (fieldMappings.containsKey(sourceFieldName)) continue;

            VariableElement sourceField = sourceFields.get(sourceFieldName);
            VariableElement targetField = targetFields.get(sourceFieldName);

            if (targetField != null) {
                if (isTypeCompatible(sourceField.asType(), targetField.asType())) {
                    String sourceGetter = getAccessorName(sourceField, true);
                    String targetSetter = getAccessorName(targetField, false);

                    if (copyNulls) {
                        codeBlock.add("  target.$L(source.$L());\n", targetSetter, sourceGetter);
                    } else {
                        codeBlock.add("  if (null != source.$L()) {\n", sourceGetter);
                        codeBlock.add("    target.$L(source.$L());\n", targetSetter, sourceGetter);
                        codeBlock.add("  }\n");
                    }
                }
            }
        }

        // 6. 处理显式配置的字段映射
        for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
            String sourceFieldName = mapping.getKey();
            String targetFieldName = mapping.getValue();

            if (ignoredFields.contains(sourceFieldName)) continue;

            VariableElement sourceField = sourceFields.get(sourceFieldName);
            VariableElement targetField = targetFields.get(targetFieldName);

            if (sourceField == null) {
                messager.printMessage(Diagnostic.Kind.WARNING, "源字段不存在: " + sourceFieldName, sourceTypeElement);
                continue;
            }

            if (targetField == null) {
                messager.printMessage(Diagnostic.Kind.WARNING, "目标字段不存在: " + targetFieldName, targetTypeElement);
                continue;
            }

            if (!isTypeCompatible(sourceField.asType(), targetField.asType())) {
                messager.printMessage(Diagnostic.Kind.WARNING, "字段类型不兼容: " + sourceFieldName + " -> " + targetFieldName, sourceTypeElement);
                continue;
            }

            String sourceGetter = getAccessorName(sourceField, true);
            String targetSetter = getAccessorName(targetField, false);
            if (copyNulls) {
                codeBlock.add("  target.$L(source.$L());\n", targetSetter, sourceGetter);
            } else {
                codeBlock.add("  if (null != source.$L()) {\n", sourceGetter);
                codeBlock.add("    target.$L(source.$L());\n", targetSetter, sourceGetter);
                codeBlock.add("  }\n");
            }
        }
        codeBlock.add("}");
        builder.addCode(codeBlock.build());
    }

    /**
     * 检查类型兼容性：
     * 1. 完全相同类型
     * 2. 基本类型与对应包装类型
     * 3. 兼容类型映射（如String和CharSequence）
     * 4. 子类到父类的赋值
     */
    private boolean isTypeCompatible(TypeMirror sourceType, TypeMirror targetType) {
        // 1. 类型完全相同
        if (typeUtils.isSameType(sourceType, targetType)) {
            return true;
        }
        // 2. 基本类型与包装类型的兼容
        String sourceName = sourceType.toString();
        String targetName = targetType.toString();
        // 基本类型到包装类型
        if (primitiveToWrapper.containsKey(sourceName) &&
                primitiveToWrapper.get(sourceName).equals(targetName)) {
            return true;
        }
        // 包装类型到基本类型
        if (primitiveToWrapper.containsKey(targetName) &&
                primitiveToWrapper.get(targetName).equals(sourceName)) {
            return true;
        }
        // 3. 兼容类型映射
        Set<String> sourceCompatible = compatibleTypes.get(sourceName);
        if (sourceCompatible != null && sourceCompatible.contains(targetName)) {
            return true;
        }
        Set<String> targetCompatible = compatibleTypes.get(targetName);
        if (targetCompatible != null && targetCompatible.contains(sourceName)) {
            return true;
        }
        // 4. 子类到父类的赋值
        if (typeUtils.isAssignable(sourceType, targetType)) {
            return true;
        }
        // 5. 数组类型兼容（长度相同的基本类型数组）
        if (sourceType.getKind() == TypeKind.ARRAY &&
                targetType.getKind() == TypeKind.ARRAY) {
            TypeMirror sourceComponent = ((ArrayType) sourceType).getComponentType();
            TypeMirror targetComponent = ((ArrayType) targetType).getComponentType();
            return isTypeCompatible(sourceComponent, targetComponent);
        }
        return false;
    }

    /**
     * 获取字段的访问器方法名
     * @param field 字段元素
     * @param isGetter true: getter方法, false: setter方法
     */
    private String getAccessorName(VariableElement field, boolean isGetter) {
        String fieldName = field.getSimpleName().toString();
        String prefix = isGetter ? (field.asType().getKind() == TypeKind.BOOLEAN ? "is" : "get") : "set";
        return prefix + capitalize(fieldName);
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private void collectUsageInstructions(TypeElement enclosingClass,
                                          List<ExecutableElement> methods,
                                          String helperClassName) {
        String className = enclosingClass.getQualifiedName().toString();
        List<String> instructions = new ArrayList<>();

        instructions.add("自动生成的对象拷贝Copier类: " + helperClassName);
        instructions.add("支持自动映射:");
        instructions.add("  - 同名同类型字段");
        instructions.add("  - 基本类型与包装类型");
        instructions.add("  - 常用类型转换(String/CharSequence, Date/时间类等)");
        instructions.add("请在以下方法中调用对应的Helper方法:");

        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();
            String helperMethodName = methodName;

            instructions.add(String.format(" - 方法 %s::%s 替换为: %s.%s(source, target)",
                    className,
                    methodName,
                    helperClassName,
                    helperMethodName
            ));
        }

        usageInstructions.put(className, instructions);
    }

    private void printUsageInstructions() {
        if (usageInstructions.isEmpty()) return;

        messager.printMessage(Diagnostic.Kind.NOTE, "============================================================");
        messager.printMessage(Diagnostic.Kind.NOTE, "              增强版对象拷贝助手使用说明");
        messager.printMessage(Diagnostic.Kind.NOTE, "============================================================");
        messager.printMessage(Diagnostic.Kind.NOTE, "支持特性:");
        messager.printMessage(Diagnostic.Kind.NOTE, "1. 同名同类型字段自动复制");
        messager.printMessage(Diagnostic.Kind.NOTE, "2. 基本类型与包装类型自动兼容");
        messager.printMessage(Diagnostic.Kind.NOTE, "3. 常用类型自动转换:");
        messager.printMessage(Diagnostic.Kind.NOTE, "   - String ↔ CharSequence");
        messager.printMessage(Diagnostic.Kind.NOTE, "   - java.util.Date ↔ java.time.*");
        messager.printMessage(Diagnostic.Kind.NOTE, "   - 子类到父类的赋值");
        messager.printMessage(Diagnostic.Kind.NOTE, "4. 显式字段映射配置");
        messager.printMessage(Diagnostic.Kind.NOTE, "5. 空值处理控制");
        messager.printMessage(Diagnostic.Kind.NOTE, "------------------------------------------------------------");
        for (Map.Entry<String, List<String>> entry : usageInstructions.entrySet()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "处理类: " + entry.getKey());
            for (String instruction : entry.getValue()) {
                messager.printMessage(Diagnostic.Kind.NOTE, instruction);
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "");
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "操作步骤:");
        messager.printMessage(Diagnostic.Kind.NOTE, "1. 打开被@ObjectCopy注解的方法");
        messager.printMessage(Diagnostic.Kind.NOTE, "2. 将方法体替换为对应的Helper方法调用");
        messager.printMessage(Diagnostic.Kind.NOTE, "3. 保存并重新编译");
        messager.printMessage(Diagnostic.Kind.NOTE, "============================================================");
    }
}
