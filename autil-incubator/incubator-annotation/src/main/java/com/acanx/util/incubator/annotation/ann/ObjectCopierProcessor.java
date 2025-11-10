package com.acanx.util.incubator.annotation.ann;

import com.acanx.util.incubator.annotation.ObjectCopier;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.acanx.util.incubator.annotation.ObjectCopier")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ObjectCopierProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(javax.annotation.processing.ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        note("开始处理 @ObjectCopier 注解", null);

        Map<TypeElement, List<ExecutableElement>> classMethodsMap = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ObjectCopier.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                error("注解@ObjectCopier只能应用于方法", element, getAnnotationMirror(element));
                continue;
            }
            ExecutableElement method = (ExecutableElement) element;
            TypeElement enclosingClass = (TypeElement) method.getEnclosingElement();
            classMethodsMap.computeIfAbsent(enclosingClass, k -> new ArrayList<>()).add(method);

            note("找到被注解的方法: " + enclosingClass.getSimpleName() + "." + method.getSimpleName(), method);
        }

        for (Map.Entry<TypeElement, List<ExecutableElement>> entry : classMethodsMap.entrySet()) {
            TypeElement enclosingClass = entry.getKey();
            List<ExecutableElement> methods = entry.getValue();
            for (ExecutableElement method : methods) {
                try {
                    generateCopierClass(method);
                } catch (Exception e) {
                    error("生成拷贝类时出错: " + e.getMessage(), method, getAnnotationMirror(method));
                }
            }
        }

        note("注解处理完成", null);
        return true;
    }

    private void generateCopierClass(ExecutableElement methodElement) throws IOException {
        if (!validateMethod(methodElement)) {
            return;
        }

        VariableElement sourceParam = methodElement.getParameters().get(0);
        VariableElement targetParam = methodElement.getParameters().get(1);
        TypeMirror sourceType = sourceParam.asType();
        TypeMirror targetType = targetParam.asType();

        if (!(sourceType instanceof DeclaredType) || !(targetType instanceof DeclaredType)) {
            error("源类型和目标类型必须是类类型", methodElement, getAnnotationMirror(methodElement));
            return;
        }

        TypeElement sourceElement = (TypeElement) ((DeclaredType) sourceType).asElement();
        TypeElement targetElement = (TypeElement) ((DeclaredType) targetType).asElement();
        TypeElement enclosingClass = (TypeElement) methodElement.getEnclosingElement();

        // 生成类名：使用被注解方法所在类的类名 + "Copier"
        String enclosingClassName = enclosingClass.getSimpleName().toString();
        String copierClassName = enclosingClassName + "Copier";

        // 获取包名
        String packageName = elementUtils.getPackageOf(enclosingClass).getQualifiedName().toString();

        // 创建源文件
        JavaFileObject builderFile = filer.createSourceFile(
                packageName + "." + copierClassName,
                methodElement
        );

        note("生成拷贝类: " + packageName + "." + copierClassName, methodElement);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            writeCopierClass(out, packageName, copierClassName, sourceElement, targetElement, methodElement, enclosingClass);
        }
    }

    private void writeCopierClass(PrintWriter out, String packageName, String className,
                                  TypeElement sourceElement, TypeElement targetElement,
                                  ExecutableElement methodElement, TypeElement enclosingClass) {

        String sourceTypeName = sourceElement.getSimpleName().toString();
        String targetTypeName = targetElement.getSimpleName().toString();
        String sourceQualifiedName = sourceElement.getQualifiedName().toString();
        String targetQualifiedName = targetElement.getQualifiedName().toString();
        String enclosingClassQualifiedName = enclosingClass.getQualifiedName().toString();
        String methodName = methodElement.getSimpleName().toString();

        // 包声明
        out.println("package " + packageName + ";");
        out.println();

        // 导入语句
        out.println("import " + sourceQualifiedName + ";");
        out.println("import " + targetQualifiedName + ";");
        out.println();

        // 类注释
        out.println("/**");
        out.println(" * 自动生成的拷贝类");
        out.println(" * 将 " + sourceTypeName + " 对象的属性拷贝到 " + targetTypeName + " 对象");
        out.println(" * 由 {@link " + enclosingClassQualifiedName + "#" + methodName + "(" + sourceTypeName + ", " + targetTypeName + ")} 方法生成");
        out.println(" */");

        // 类声明
        out.println("public class " + className + " {");
        out.println();

        // 拷贝方法
        out.println("    /**");
        out.println("     * 执行属性拷贝");
        out.println("     * 由 {@link " + enclosingClassQualifiedName + "#" + methodName + "(" + sourceTypeName + ", " + targetTypeName + ")} 方法生成");
        out.println("     * @param source 源对象");
        out.println("     * @param target 目标对象");
        out.println("     */");
        out.println("    public static void " + methodName + "(" + sourceTypeName + " source, " + targetTypeName + " target) {");
        out.println("        if (source == null || target == null) {");
        out.println("            return;");
        out.println("        }");
        out.println();
        // 生成属性拷贝代码
        List<PropertyMapping> mappings = findPropertyMappings(sourceElement, targetElement);
        if (mappings.isEmpty()) {
            out.println("        // 未找到可拷贝的属性");
        } else {
            out.println("        // 属性拷贝");
            for (PropertyMapping mapping : mappings) {
                out.println("        target." + mapping.setterCall.replace("(value)", "(" + "source." + mapping.getterCall + ")" + ";"));
            }
        }
        out.println("    }");
        out.println("}");
    }

    private List<PropertyMapping> findPropertyMappings(TypeElement sourceElement, TypeElement targetElement) {
        List<PropertyMapping> mappings = new ArrayList<>();
        List<GetterMethod> sourceGetters = findGetterMethods(sourceElement);
        List<SetterMethod> targetSetters = findSetterMethods(targetElement);

        note("在 " + sourceElement.getSimpleName() + " 中找到 " + sourceGetters.size() + " 个getter方法", sourceElement);
        note("在 " + targetElement.getSimpleName() + " 中找到 " + targetSetters.size() + " 个setter方法", targetElement);

        for (GetterMethod getter : sourceGetters) {
            SetterMethod setter = findMatchingSetter(targetSetters, getter.propertyName, getter.returnType);
            if (setter != null) {
                mappings.add(new PropertyMapping(getter.propertyName, getter.methodCall, setter.methodCall));
                note("属性映射: " + getter.propertyName + " (" + getSimpleTypeName(getter.returnType) + ")", sourceElement);
            }
        }

        note("总共建立 " + mappings.size() + " 个属性映射", sourceElement);
        return mappings;
    }

    private String extractPropertyName(String methodName) {
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            String baseName = methodName.substring(3);
            if (!baseName.isEmpty()) {
                return Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1);
            }
        } else if (methodName.startsWith("is")) {
            String baseName = methodName.substring(2);
            if (!baseName.isEmpty()) {
                return Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1);
            }
        }
        return methodName;
    }

    private List<GetterMethod> findGetterMethods(TypeElement element) {
        List<GetterMethod> getters = new ArrayList<>();
        TypeMirror currentType = element.asType();

        while (currentType.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) currentType;
            TypeElement currentElement = (TypeElement) declaredType.asElement();

            for (Element enclosed : currentElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.METHOD && enclosed instanceof ExecutableElement) {
                    ExecutableElement method = (ExecutableElement) enclosed;
                    String methodName = method.getSimpleName().toString();

                    if (isGetterMethod(method, methodName)) {
                        String propertyName = extractPropertyName(methodName);
                        String methodCall = methodName + "()";
                        getters.add(new GetterMethod(propertyName, methodCall, method.getReturnType()));
                    }
                }
            }

            currentType = currentElement.getSuperclass();
            if (currentType.getKind() == TypeKind.NONE) {
                break;
            }
        }
        return getters;
    }

    private boolean isGetterMethod(ExecutableElement method, String methodName) {
        if (method.getModifiers().contains(Modifier.STATIC)) return false;
        if (!method.getParameters().isEmpty()) return false;

        if (methodName.startsWith("get") && methodName.length() > 3) return true;
        return methodName.startsWith("is") && methodName.length() > 2 &&
                method.getReturnType().getKind() == TypeKind.BOOLEAN;
    }

    private List<SetterMethod> findSetterMethods(TypeElement element) {
        List<SetterMethod> setters = new ArrayList<>();
        TypeMirror currentType = element.asType();

        while (currentType.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) currentType;
            TypeElement currentElement = (TypeElement) declaredType.asElement();

            for (Element enclosed : currentElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.METHOD && enclosed instanceof ExecutableElement) {
                    ExecutableElement method = (ExecutableElement) enclosed;
                    String methodName = method.getSimpleName().toString();

                    if (isSetterMethod(method, methodName)) {
                        String propertyName = extractPropertyName(methodName);
                        String methodCall = methodName + "(value)";
                        setters.add(new SetterMethod(propertyName, methodCall, method.getParameters().get(0).asType()));
                    }
                }
            }

            currentType = currentElement.getSuperclass();
            if (currentType.getKind() == TypeKind.NONE) {
                break;
            }
        }
        return setters;
    }

    private boolean isSetterMethod(ExecutableElement method, String methodName) {
        if (method.getModifiers().contains(Modifier.STATIC)) return false;
        return methodName.startsWith("set") && methodName.length() > 3 &&
                method.getParameters().size() == 1;
    }

    private SetterMethod findMatchingSetter(List<SetterMethod> setters, String propertyName, TypeMirror getterType) {
        for (SetterMethod setter : setters) {
            if (setter.propertyName.equals(propertyName) && isTypeCompatible(getterType, setter.paramType)) {
                return setter;
            }
        }
        return null;
    }

    private boolean isTypeCompatible(TypeMirror sourceType, TypeMirror targetType) {
        return typeUtils.isAssignable(sourceType, targetType) ||
                typeUtils.isAssignable(targetType, sourceType);
    }

    private boolean validateMethod(ExecutableElement method) {
        if (method.getParameters().size() != 2) {
            error("@ObjectCopier注解的方法必须恰好有2个参数", method, getAnnotationMirror(method));
            return false;
        }
        if (!method.getReturnType().getKind().equals(TypeKind.VOID)) {
            error("@ObjectCopier注解的方法必须返回void", method, getAnnotationMirror(method));
            return false;
        }
        return true;
    }

    private String getSimpleTypeName(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
            return typeElement.getSimpleName().toString();
        }
        return typeMirror.toString();
    }

    private void error(String msg, Element element, AnnotationMirror annotationMirror) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element, annotationMirror);
    }

    private void warn(String msg, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, element);
    }

    private void note(String msg, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, element);
    }

    private AnnotationMirror getAnnotationMirror(Element element) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(ObjectCopier.class.getName())) {
                return mirror;
            }
        }
        return null;
    }

    // 内部类保持不变...
    private static class PropertyMapping {
        final String propertyName;
        final String getterCall;
        final String setterCall;

        PropertyMapping(String propertyName, String getterCall, String setterCall) {
            this.propertyName = propertyName;
            this.getterCall = getterCall;
            this.setterCall = setterCall;
        }
    }

    private static class GetterMethod {
        final String propertyName;
        final String methodCall;
        final TypeMirror returnType;

        GetterMethod(String propertyName, String methodCall, TypeMirror returnType) {
            this.propertyName = propertyName;
            this.methodCall = methodCall;
            this.returnType = returnType;
        }
    }

    private static class SetterMethod {
        final String propertyName;
        final String methodCall;
        final TypeMirror paramType;

        SetterMethod(String propertyName, String methodCall, TypeMirror paramType) {
            this.propertyName = propertyName;
            this.methodCall = methodCall;
            this.paramType = paramType;
        }
    }
}