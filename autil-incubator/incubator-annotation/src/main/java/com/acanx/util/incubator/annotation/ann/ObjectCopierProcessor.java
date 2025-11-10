package com.acanx.util.incubator.annotation.ann;

/**
 * ObjectCopierProcessor
 *
 * @author ACANX
 * @since 20251110
 */


import com.acanx.util.incubator.annotation.ObjectCopier;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 对象拷贝注解处理器
 * 通过修改AST在编译期增强被注解的方法
 */
@SupportedAnnotationTypes("com.acanx.util.incubator.annotation.ObjectCopier")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class ObjectCopierProcessor extends AbstractProcessor {

    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        try {
            // 使用反射获取Context
            Context context = getContext(processingEnv);
            this.treeMaker = TreeMaker.instance(context);
            this.names = Names.instance(context);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to initialize compiler internals: " + e.getMessage()
            );
        }
    }



    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(ObjectCopier.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@ObjectCopier can only be applied to methods",
                        element
                );
                continue;
            }
            ExecutableElement methodElement = (ExecutableElement) element;
            // 验证方法参数
            if (!validateMethod(methodElement)) {
                continue;
            }
            // 获取方法对应的AST
            MethodTree methodTree = trees.getTree(methodElement);
            if (methodTree instanceof JCTree.JCMethodDecl) {
                enhanceMethod((JCTree.JCMethodDecl) methodTree, methodElement);
            }
        }
        return true;
    }

    /**
     * 通过反射获取Compiler Context
     */
    private Context getContext(ProcessingEnvironment processingEnv) throws Exception {
        // 方法1: 通过JavacTask获取Context
        if (processingEnv instanceof JavacTask) {
            JavacTask javacTask = (JavacTask) processingEnv;
            // 尝试通过getContext方法获取
            try {
                Method getContextMethod = javacTask.getClass().getMethod("getContext");
                return (Context) getContextMethod.invoke(javacTask);
            } catch (NoSuchMethodException e) {
                // 如果getContext方法不存在，尝试其他方式
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "getContext() method not found, trying alternative approach"
                );
            }
        }
        // 方法2: 通过Trees获取Context
        try {
            Method getContextMethod = trees.getClass().getMethod("getContext");
            return (Context) getContextMethod.invoke(trees);
        } catch (NoSuchMethodException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Trees.getContext() not available, trying field access"
            );
        }
        // 方法3: 通过字段访问（最后的手段）
        return getContextViaFieldAccess(processingEnv);
    }

    /**
     * 在类层次结构中查找字段
     */
    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 通过字段反射获取Context
     */
    private Context getContextViaFieldAccess(ProcessingEnvironment processingEnv) throws Exception {
        // 获取JavacTask实例
        if (processingEnv instanceof JavacTask) {
            JavacTask javacTask = (JavacTask) processingEnv;
            // 尝试访问context字段
            try {
                java.lang.reflect.Field contextField = findField(javacTask.getClass(), "context");
                if (contextField != null) {
                    contextField.setAccessible(true);
                    return (Context) contextField.get(javacTask);
                }
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.WARNING,
                        "Field access failed: " + e.getMessage()
                );
            }
        }
        throw new IllegalStateException("Unable to access compiler Context");
    }


    /**
     * 验证方法是否符合要求
     */
    private boolean validateMethod(ExecutableElement method) {
        // 验证参数数量
        if (method.getParameters().size() != 2) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@ObjectCopier method must have exactly 2 parameters",
                    method
            );
            return false;
        }
        // 验证方法可见性
        if (!method.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE)) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "@ObjectCopier is recommended to be used on private methods for better encapsulation",
                    method
            );
        }
        // 验证返回类型为void
        TypeMirror returnType = method.getReturnType();
        if (!returnType.toString().equals("void")) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@ObjectCopier method must have void return type",
                    method
            );
            return false;
        }
        return true;
    }

    /**
     * 增强方法，添加对象拷贝逻辑
     */
    private void enhanceMethod(JCTree.JCMethodDecl methodTree, ExecutableElement methodElement) {
        ObjectCopier annotation = methodElement.getAnnotation(ObjectCopier.class);
        // 获取源参数和目标参数
        JCTree.JCVariableDecl sourceParam = methodTree.getParameters().get(0);
        JCTree.JCVariableDecl targetParam = methodTree.getParameters().get(1);
        // 创建拷贝语句
        List<JCTree.JCStatement> copyStatements = createCopyStatements(
                sourceParam,
                targetParam,
                annotation,
                methodElement
        );
        // 替换方法体
        JCTree.JCBlock newBody = treeMaker.Block(0, copyStatements);
        methodTree.body = newBody;
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "Enhanced @ObjectCopier method: " + methodElement.getSimpleName(),
                methodElement
        );
    }

    /**
     * 创建拷贝语句
     */
    private List<JCTree.JCStatement> createCopyStatements(
            JCTree.JCVariableDecl sourceParam,
            JCTree.JCVariableDecl targetParam,
            ObjectCopier annotation,
            ExecutableElement methodElement) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        // 添加空值检查
        statements.append(createNullCheckStatement(sourceParam, "Source object cannot be null"));
        statements.append(createNullCheckStatement(targetParam, "Target object cannot be null"));
        // 获取源对象类型的所有字段
        TypeMirror sourceType = methodElement.getParameters().get(0).asType();
        TypeElement sourceTypeElement = (TypeElement) ((DeclaredType) sourceType).asElement();
        // 为每个字段生成赋值语句
        for (Element field : sourceTypeElement.getEnclosedElements()) {
            if (field.getKind() == ElementKind.FIELD &&
                    !field.getModifiers().contains(javax.lang.model.element.Modifier.STATIC)) {
                JCTree.JCStatement assignment = createFieldAssignment(
                        sourceParam,
                        targetParam,
                        field,
                        annotation
                );
                if (assignment != null) {
                    statements.append(assignment);
                }
            }
        }
        return statements.toList();
    }

    /**
     * 创建空值检查语句
     */
    private JCTree.JCStatement createNullCheckStatement(JCTree.JCVariableDecl param, String message) {
        // 创建条件表达式: param == null
        JCTree.JCExpression nullCheck = treeMaker.Binary(
                JCTree.Tag.EQ,
                treeMaker.Ident(param.name),
                treeMaker.Literal(null)
        );
        // 创建异常抛出语句
        JCTree.JCExpression exceptionType = treeMaker.Ident(names.fromString("IllegalArgumentException"));
        JCTree.JCNewClass newException = treeMaker.NewClass(
                null,
                List.nil(),
                exceptionType,
                List.of(treeMaker.Literal(message)),
                null
        );
        JCTree.JCThrow throwStatement = treeMaker.Throw(newException);
        // 创建if语句
        return treeMaker.If(nullCheck, throwStatement, null);
    }

    /**
     * 创建字段赋值语句
     */
    private JCTree.JCStatement createFieldAssignment(
            JCTree.JCVariableDecl sourceParam,
            JCTree.JCVariableDecl targetParam,
            Element field,
            ObjectCopier annotation) {
        String fieldName = field.getSimpleName().toString();
        // 检查字段是否在排除列表中
        if (isFieldExcluded(fieldName, annotation)) {
            return null;
        }
        // 检查字段是否在包含列表中（如果设置了include）
        if (annotation.include().length > 0 && !isFieldIncluded(fieldName, annotation)) {
            return null;
        }
        // 创建字段访问表达式: target.field
        JCTree.JCFieldAccess targetFieldAccess = treeMaker.Select(
                treeMaker.Ident(targetParam.name),
                names.fromString(fieldName)
        );
        // 创建字段访问表达式: source.field
        JCTree.JCFieldAccess sourceFieldAccess = treeMaker.Select(
                treeMaker.Ident(sourceParam.name),
                names.fromString(fieldName)
        );
        JCTree.JCExpression assignmentExpression;
        if (annotation.ignoreNull()) {
            // 如果忽略空值，创建条件赋值: source.field != null ? source.field : target.field
            JCTree.JCExpression nullCheck = treeMaker.Binary(
                    JCTree.Tag.NE,
                    sourceFieldAccess,
                    treeMaker.Literal(null)
            );
            assignmentExpression = treeMaker.Conditional(
                    nullCheck,
                    sourceFieldAccess,
                    treeMaker.Ident(targetParam.name) // 保持原值
            );
        } else {
            // 直接赋值
            assignmentExpression = sourceFieldAccess;
        }
        // 创建赋值语句: target.field = assignmentExpression
        return treeMaker.Exec(
                treeMaker.Assign(targetFieldAccess, assignmentExpression)
        );
    }

    private boolean isFieldExcluded(String fieldName, ObjectCopier annotation) {
        for (String excluded : annotation.exclude()) {
            if (excluded.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFieldIncluded(String fieldName, ObjectCopier annotation) {
        for (String included : annotation.include()) {
            if (included.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }


}