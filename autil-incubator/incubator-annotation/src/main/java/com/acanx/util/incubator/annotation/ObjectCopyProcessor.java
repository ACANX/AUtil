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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;

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
                return false;
            }
            // 获取所有被@ObjectCopy注解的元素
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ObjectCopy.class);
            // 过滤出方法元素
            List<ExecutableElement> methods = elements.stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .map(e -> (ExecutableElement) e)
                    .collect(Collectors.toList());
            if (methods.isEmpty()) {
                return false;
            }
            // 按类分组处理方法
            Map<TypeElement, List<ExecutableElement>> methodsByClass = methods.stream()
                    .collect(Collectors.groupingBy(method -> (TypeElement) method.getEnclosingElement()));
            // 处理每个类
            for (Map.Entry<TypeElement, List<ExecutableElement>> entry : methodsByClass.entrySet()) {
                TypeElement enclosingClass = entry.getKey();
                List<ExecutableElement> classMethods = entry.getValue();
                // 生成Helper类名
                String objectCopierClassName = enclosingClass.getSimpleName().toString() + SUFFIX;
                // 创建Helper类
                createHelperClass(enclosingClass, classMethods, objectCopierClassName);
                // 生成使用说明文档（不再尝试重建原始类）
                // generateUsageDocumentation(enclosingClass, classMethods, helperClassName);
            }

            return true;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "处理器异常: " + e.getMessage());
            return false;
        }
    }

    private void createHelperClass(TypeElement enclosingClass,
                                   List<ExecutableElement> methods,
                                   String objectCopierClassName) {
        try {
            String packageName = elementUtils.getPackageOf(enclosingClass).toString();
            String fullClassName = packageName + "." + objectCopierClassName;
            // 检查是否已生成
            if (generatedFiles.contains(fullClassName)) {
                return;
            }
            TypeSpec.Builder helperClassBuilder = TypeSpec.classBuilder(objectCopierClassName).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            // 为每个方法生成Helper方法
            for (ExecutableElement method : methods) {
                String methodName = method.getSimpleName().toString();
                ObjectCopy annotation = method.getAnnotation(ObjectCopy.class);
                MethodSpec objectCopyMethod = generateObjectCopyMethod(method, methodName, annotation);
                if (objectCopyMethod != null) {
                    helperClassBuilder.addMethod(objectCopyMethod);
                }
            }
            // 生成Helper类文件
            JavaFile.builder(packageName, helperClassBuilder.build())
                    .build()
                    .writeTo(filer);
            // 记录已生成的文件
            generatedFiles.add(fullClassName);
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "创建ObjectCopier类失败: " + e.getMessage(), enclosingClass);
        }
    }

    private MethodSpec generateObjectCopyMethod(ExecutableElement method,
                                                String helperMethodName,
                                                ObjectCopy annotation) {
        List<? extends VariableElement> parameters = method.getParameters();

        if (parameters.size() != 2) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ObjectCopy 方法必须要有两个形式参数", method);
            return null;
        }
        VariableElement sourceParam = parameters.get(0);
        VariableElement targetParam = parameters.get(1);
        TypeName sourceType = TypeName.get(sourceParam.asType());
        TypeName targetType = TypeName.get(targetParam.asType());

        MethodSpec.Builder builder = MethodSpec.methodBuilder(helperMethodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(sourceType, "source")
                .addParameter(targetType, "target");
        addFieldCopyStatements(builder, annotation, sourceParam, targetParam);
        return builder.build();
    }

    private void addFieldCopyStatements(MethodSpec.Builder builder,
                                        ObjectCopy config,
                                        VariableElement sourceParam,
                                        VariableElement targetParam) {
        boolean copyNulls = config.copyNulls();
        Set<String> ignoredFields = new HashSet<>(Arrays.asList(config.ignoreFields()));
        builder.beginControlFlow("if (source != null)");
        // 处理字段映射
        for (ObjectCopy.FieldMapping mapping : config.fieldMappings()) {
            String sourceField = mapping.source();
            String targetField = mapping.target();
            if (ignoredFields.contains(sourceField)) {
                continue;
            }
            // 添加字段赋值逻辑
            if (copyNulls) {
                builder.addStatement("target.set$L(source.get$L())", capitalize(targetField), capitalize(sourceField));
            } else {
                builder.beginControlFlow("if (source.get$L() != null)", capitalize(sourceField));
                builder.addStatement("target.set$L(source.get$L())", capitalize(targetField), capitalize(sourceField));
                builder.endControlFlow();
            }
        }
        builder.endControlFlow(); // if (source != null)
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     *    TODO 计划改为生成对应的markdown文档
     *
     * @param enclosingClass
     * @param methods
     * @param helperClassName
     */
    private void generateUsageDocumentation(TypeElement enclosingClass,
                                            List<ExecutableElement> methods,
                                            String helperClassName) {
        try {
            String packageName = elementUtils.getPackageOf(enclosingClass).toString();
            String docClassName = enclosingClass.getSimpleName().toString() + "CopierUsage";
            String fullDocClassName = packageName + "." + docClassName;

            // 检查是否已生成
            if (generatedFiles.contains(fullDocClassName)) {
                return;
            }

            // 构建文档内容
            StringBuilder javadoc = new StringBuilder();
            javadoc.append("使用说明:\n");
            javadoc.append("请将以下方法替换为Copier类调用:\n\n");

            for (ExecutableElement method : methods) {
                String methodName = method.getSimpleName().toString();
                javadoc.append("- 方法: ").append(methodName).append("\n");
                javadoc.append("  替换为: ").append(helperClassName)
                        .append(".").append(methodName).append("Copier(source, target);\n\n");
            }

            // 生成文档类
            TypeSpec docClass = TypeSpec.classBuilder(docClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc(javadoc.toString())
                    .build();

            JavaFile.builder(packageName, docClass)
                    .build()
                    .writeTo(filer);

            // 记录已生成的文件
            generatedFiles.add(fullDocClassName);

        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.WARNING, "无法生成使用文档: " + e.getMessage());
        }
    }

    // 检查文件是否已生成 (备用方法)
    private boolean isFileGenerated(String packageName, String className) {
        String fullName = packageName + "." + className;
        try {
            // 尝试获取资源，如果存在则返回true
            filer.getResource(
                    StandardLocation.SOURCE_OUTPUT,
                    packageName,
                    className + ".java"
            );
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
