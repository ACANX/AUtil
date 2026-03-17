package com.acanx.util.annotation.processor.ast;

import com.acanx.util.annotation.Copier;
import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationMirror;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 javac 内部 API 的 Copier 注解处理器
 * 直接在编译期修改原有方法的 AST，实现类似 Lombok 的字节码增强
 *
 * <p><strong>注意：</strong></p>
 * <ul>
 *     <li>此处理器仅支持 javac 编译器，不兼容 ECJ (Eclipse Compiler)</li>
 *     <li>使用 javac 内部 API ({@code com.sun.tools.javac.*})，可能在不同 JDK 版本间不兼容</li>
 *     <li>仅处理配置了 {@code generationMode = GenerationMode.BYTECODE} 的@Copier 方法</li>
 * </ul>
 *
 * @see Copier.GenerationMode#BYTECODE
 */
@SupportedAnnotationTypes("com.acanx.util.annotation.Copier")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class CopierAstProcessor extends AbstractProcessor {

    private JavacProcessingEnvironment javacEnv;
    private TreeMaker treeMaker;
    private Names names;
    private Context context;
    private Trees trees;

    @Override
    public synchronized void init(javax.annotation.processing.ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.javacEnv = (JavacProcessingEnvironment) processingEnv;
        this.context = javacEnv.getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "CopierAstProcessor: 开始处理 " + roundEnv.getElementsAnnotatedWith(Copier.class).size() + " 个元素"
        );

        // 获取 Copier 注解的类型元素
        TypeElement copierTypeElement = processingEnv.getElementUtils().getTypeElement("com.acanx.util.annotation.Copier");
        if (copierTypeElement == null) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "找不到 Copier 注解类"
            );
            return false;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Copier.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }

            ExecutableElement method = (ExecutableElement) element;
            
            // 使用 Mirror API 读取注解值，避免反射问题
            CopierConfig config = readCopierConfig(method, copierTypeElement);

            // 仅处理 BYTECODE 模式
            if (config.generationMode != Copier.GenerationMode.BYTECODE) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "跳过非 BYTECODE 模式的方法：" + method.getSimpleName()
                );
                continue;
            }

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "处理方法：" + method.getSimpleName() + " BYTECODE 模式"
            );

            // 获取方法的 AST 节点
            JCTree.JCMethodDecl methodDecl = getMethodDecl(method);
            if (methodDecl == null) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.WARNING,
                        "无法获取方法 AST: " + method.getSimpleName() + "。BYTECODE 模式需要 javac 编译器，请确认未使用 ECJ。",
                        method
                );
                continue;
            }

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "成功获取 AST，开始注入代码：" + method.getSimpleName()
            );

            // 生成拷贝代码并注入到方法体
            injectCopyCode(methodDecl, method, config);
        }
        return true;
    }

    /**
     * 获取方法的 AST 节点
     * 使用 Trees API 来获取语法树
     */
    private JCTree.JCMethodDecl getMethodDecl(ExecutableElement method) {
        try {
            // 使用 Trees API 获取方法的 AST
            TreePath path = trees.getPath(method);
            if (path == null) {
                return null;
            }

            com.sun.source.tree.Tree leaf = path.getLeaf();
            if (leaf instanceof JCTree.JCMethodDecl) {
                return (JCTree.JCMethodDecl) leaf;
            }

            return null;
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "获取方法 AST 失败：" + e.getMessage()
            );
        }
        return null;
    }

    /**
     * 向方法体注入拷贝代码
     */
    private void injectCopyCode(JCTree.JCMethodDecl methodDecl,
                                ExecutableElement method,
                                CopierConfig config) {
        // 1. 获取方法的源位置（关键！）
        long methodPos = getMethodPosition(methodDecl);
        treeMaker.at((int)methodPos);

        List<? extends VariableElement> parameters = List.from(method.getParameters());
        if (parameters.size() < 2) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@Copier 注解的方法必须至少有两个参数 (src, dest)",
                    method
            );
            return;
        }

        VariableElement srcParam = parameters.get(0);
        VariableElement destParam = parameters.get(1);

        // 2. 生成方法体（所有新节点都会使用 treeMaker 的当前位置）
        JCTree.JCBlock newBody = generateCopyBody(srcParam, destParam, config, method);

        // 3. 替换原有方法体
        methodDecl.body = newBody;
    }

    /**
     * 获取方法声明节点的开始位置
     */
    private long getMethodPosition(JCTree.JCMethodDecl methodDecl) {
        // 方法声明节点的起始位置通常保存在 methodDecl.pos 中
        // 但为了更准确，可以通过 Trees 从源文件中获取
        TreePath path = trees.getPath(methodDecl.sym);
        if (path != null) {
            return trees.getSourcePositions().getStartPosition(path.getCompilationUnit(), path.getLeaf());
        }
        // 回退到方法声明节点自身的位置（通常也是有效的）
        return methodDecl.pos;
    }

    /**
     * 生成拷贝代码的方法体
     */
    private JCTree.JCBlock generateCopyBody(VariableElement srcParam,
                                            VariableElement destParam,
                                            CopierConfig config,
                                            ExecutableElement method) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

        // 1. 添加参数 null 检查（新增部分）
        statements.append(generateNullCheck(srcParam.getSimpleName().toString(), destParam.getSimpleName().toString()));

        // 获取源类和目标类的字段
        Map<String, FieldInfo> srcFields = getAllFields(srcParam.asType());
        Map<String, FieldInfo> destFields = getAllFields(destParam.asType());

        // 找出共同的字段
        Set<String> commonFields = new HashSet<>(srcFields.keySet());
        commonFields.retainAll(destFields.keySet());

        // 处理 include/exclude
        Set<String> fieldsToCopy = filterFields(commonFields, config);

        // 为每个字段生成拷贝代码
        for (String fieldName : fieldsToCopy) {
            FieldInfo srcField = srcFields.get(fieldName);
            FieldInfo destField = destFields.get(fieldName);

            if (srcField == null || destField == null) {
                continue;
            }

            // 检查类型是否兼容
            if (!isTypeCompatible(srcField.type, destField.type)) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.WARNING,
                        "字段类型不兼容：" + fieldName +
                                " src=" + srcField.type + " dest=" + destField.type,
                        method
                );
                continue;
            }

            generateFieldCopy(statements, srcParam.getSimpleName().toString(),
                    destParam.getSimpleName().toString(), fieldName,
                    srcField, destField, config);
        }

        return treeMaker.Block(0, statements.toList());
    }

    /**
     * 生成参数空值检查语句：if (null == src || null == dest) return;
     */
    private JCTree.JCIf generateNullCheck(String srcParam, String destParam) {
        // null == src   (注意：null 放在前面)
        JCTree.JCExpression srcNull = treeMaker.Binary(
                JCTree.Tag.EQ,
                treeMaker.Literal(TypeTag.BOT, null),   // null 字面量放在前面
                treeMaker.Ident(names.fromString(srcParam))
        );
        // null == dest
        JCTree.JCExpression destNull = treeMaker.Binary(
                JCTree.Tag.EQ,
                treeMaker.Literal(TypeTag.BOT, null),
                treeMaker.Ident(names.fromString(destParam))
        );
        // (null == src) || (null == dest)
        JCTree.JCExpression condition = treeMaker.Binary(
                JCTree.Tag.OR,
                srcNull,
                destNull
        );
        // return; （void 方法返回空）
        JCTree.JCStatement returnStmt = treeMaker.Return(null);
        // if (null == src || null == dest) return;
        return treeMaker.If(condition, returnStmt, null);
    }

    /**
     * 生成字段拷贝代码 - 简化版本
     */
    private void generateFieldCopy(ListBuffer<JCTree.JCStatement> statements,
                                   String srcParamName,
                                   String destParamName,
                                   String fieldName,
                                   FieldInfo srcField,
                                   FieldInfo destField,
                                   CopierConfig config) {

        boolean useAccessors = config.useAccessors;
        TypeMirror srcFieldType = srcField.type;

        // 1. 构建源字段访问表达式：src.getField() 或 src.field
        JCTree.JCExpression srcAccess;
        if (useAccessors) {
            String srcGetter = "get" + capitalize(fieldName);
            srcAccess = treeMaker.Apply(
                    List.nil(),
                    treeMaker.Select(
                            treeMaker.Ident(names.fromString(srcParamName)),
                            names.fromString(srcGetter)
                    ),
                    List.nil()
            );
        } else {
            srcAccess = treeMaker.Select(
                    treeMaker.Ident(names.fromString(srcParamName)),
                    names.fromString(fieldName)
            );
        }

        // 2. 构建目标赋值参数：若为集合类型则深拷贝
        JCTree.JCExpression assignValue;
        if (isCollectionType(srcFieldType)) {
            // 生成 new java.util.ArrayList<>(srcAccess)
            assignValue = treeMaker.NewClass(
                    null,                                         // enclosing expression
                    List.nil(),                                   // type arguments
                    buildJavaUtilArrayListType(),                 // java.util.ArrayList
                    List.of(srcAccess),                           // constructor arguments
                    null                                          // class body (not needed)
            );
        } else {
            assignValue = srcAccess;
        }

        // 3. 构建目标赋值语句：dest.setField(value) 或 dest.field = value
        JCTree.JCStatement assignStmt;
        if (useAccessors) {
            String destSetter = "set" + capitalize(fieldName);
            assignStmt = treeMaker.Exec(
                    treeMaker.Apply(
                            List.nil(),
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString(destParamName)),
                                    names.fromString(destSetter)
                            ),
                            List.of(assignValue)
                    )
            );
        } else {
            assignStmt = treeMaker.Exec(
                    treeMaker.Assign(
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString(destParamName)),
                                    names.fromString(fieldName)
                            ),
                            assignValue
                    )
            );
        }

        // 4. 构建条件表达式：null != src.getXxx()   (null 放在前面)
        JCTree.JCExpression condition = treeMaker.Binary(
                JCTree.Tag.NE,                     // !=
                treeMaker.Literal(TypeTag.BOT, null),  // null 字面量放左边
                srcAccess                               // srcAccess 放右边
        );

        // 5. 生成 if 语句
        JCTree.JCIf ifStmt = treeMaker.If(
                condition,
                assignStmt,                         // then 部分
                null                                 // else 部分
        );

        // 6. 添加到方法体语句列表
        statements.append(ifStmt);
    }

    // 判断是否为集合类型（简化版，可根据需要扩展）
    private boolean isCollectionType(TypeMirror type) {
        TypeElement collectionElem = processingEnv.getElementUtils().getTypeElement("java.util.Collection");
        return collectionElem != null && processingEnv.getTypeUtils().isAssignable(type, collectionElem.asType());
    }

    // 构建 java.util.ArrayList 类型表达式
    private JCTree.JCExpression buildJavaUtilArrayListType() {
        // java.util.ArrayList 的全限定名表达式
        return treeMaker.Select(
                treeMaker.Select(
                        treeMaker.Ident(names.fromString("java")),
                        names.fromString("util")
                ),
                names.fromString("ArrayList")
        );
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

    private Set<String> filterFields(Set<String> fields, CopierConfig config) {
        Set<String> result = new HashSet<>(fields);

        String[] include = config.include;
        if (include.length > 0) {
            result.retainAll(Arrays.stream(include).collect(Collectors.toSet()));
        }

        String[] exclude = config.exclude;
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
        if (javacEnv.getTypeUtils().isSameType(srcType, destType)) {
            return true;
        }
        if (javacEnv.getTypeUtils().isSubtype(srcType, destType)) {
            return true;
        }
        if (srcType.getKind().isPrimitive() || destType.getKind().isPrimitive()) {
            return true;
        }
        return false;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 读取 Copier 注解配置
     */
    private CopierConfig readCopierConfig(ExecutableElement method, TypeElement copierTypeElement) {
        try {
            Copier.GenerationMode generationMode = getEnumAnnotationValue(method, copierTypeElement, "generationMode", Copier.GenerationMode.BYTECODE);
            boolean useAccessors = getBooleanAnnotationValue(method, copierTypeElement, "useAccessors", true);
            boolean ignoreNull = getBooleanAnnotationValue(method, copierTypeElement, "ignoreNull", false);
            String[] include = getStringArrayAnnotationValue(method, copierTypeElement, "include");
            String[] exclude = getStringArrayAnnotationValue(method, copierTypeElement, "exclude");
            
            return new CopierConfig(generationMode, useAccessors, ignoreNull, include != null ? include : new String[0], exclude != null ? exclude : new String[0]);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "读取 Copier 配置失败：" + e.getMessage() + ", 使用默认配置"
            );
            return CopierConfig.defaultValue();
        }
    }

    /**
     * 从注解中读取枚举类型的值
     */
    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T getEnumAnnotationValue(ExecutableElement method, TypeElement annotationType, 
                                                          String methodName, T defaultValue) {
        try {
            AnnotationValue value = getAnnotationValueFromType(method, annotationType, methodName);
            if (value != null) {
                VariableElement enumConstant = (VariableElement) value.getValue();
                return (T) Enum.valueOf(defaultValue.getDeclaringClass(), enumConstant.getSimpleName().toString());
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 从注解中读取 boolean 类型的值
     */
    private boolean getBooleanAnnotationValue(ExecutableElement method, TypeElement annotationType, 
                                               String methodName, boolean defaultValue) {
        try {
            AnnotationValue value = getAnnotationValueFromType(method, annotationType, methodName);
            if (value != null) {
                return (Boolean) value.getValue();
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 从注解中读取 String 数组类型的值
     */
    private String[] getStringArrayAnnotationValue(ExecutableElement method, TypeElement annotationType, 
                                                    String methodName) {
        try {
            AnnotationValue value = getAnnotationValueFromType(method, annotationType, methodName);
            if (value != null) {
                @SuppressWarnings("unchecked")
                List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) value.getValue();
                return list.stream()
                        .map(v -> (String) v.getValue())
                        .toArray(String[]::new);
            }
            return new String[0];
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * 获取注解值
     */
    private AnnotationValue getAnnotationValueFromType(ExecutableElement method, TypeElement annotationType, String methodName) {
        for (AnnotationMirror annotationMirror : method.getAnnotationMirrors()) {
            // 检查是否是正确的注解类型
            if (!annotationMirror.getAnnotationType().toString().equals(annotationType.getQualifiedName().toString())) {
                continue;
            }
            
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : 
                    annotationMirror.getElementValues().entrySet()) {
                if (entry.getKey().getSimpleName().contentEquals(methodName)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 字段信息
     */
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

    /**
     * Copier 注解配置
     */
    private static class CopierConfig {
        final Copier.GenerationMode generationMode;
        final boolean useAccessors;
        final boolean ignoreNull;
        final String[] include;
        final String[] exclude;

        CopierConfig(Copier.GenerationMode generationMode, boolean useAccessors, boolean ignoreNull, 
                     String[] include, String[] exclude) {
            this.generationMode = generationMode;
            this.useAccessors = useAccessors;
            this.ignoreNull = ignoreNull;
            this.include = include;
            this.exclude = exclude;
        }

        static CopierConfig defaultValue() {
            return new CopierConfig(Copier.GenerationMode.BYTECODE, true, false, new String[0], new String[0]);
        }
    }
}