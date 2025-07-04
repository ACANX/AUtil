//package com.acanx.util.incubator.annotation;
//
//
//import com.acanx.annotation.ObjectCopy;
//import com.google.auto.service.AutoService;
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.Label;
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//import org.objectweb.asm.Type;
//
//import javax.annotation.processing.AbstractProcessor;
//import javax.annotation.processing.Filer;
//import javax.annotation.processing.Processor;
//import javax.annotation.processing.RoundEnvironment;
//import javax.annotation.processing.SupportedAnnotationTypes;
//import javax.annotation.processing.SupportedSourceVersion;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ElementKind;
//import javax.lang.model.element.ExecutableElement;
//import javax.lang.model.element.TypeElement;
//import javax.lang.model.type.ArrayType;
//import javax.lang.model.type.DeclaredType;
//import javax.lang.model.type.TypeMirror;
//import javax.tools.Diagnostic;
//import javax.tools.FileObject;
//import javax.tools.StandardLocation;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * ObjectValueCopyProcessor
// *
// * @author ACANX
// * @since 202506
// */
//@SupportedAnnotationTypes("com.acanx.annotation.ObjectCopy")
//@SupportedSourceVersion(SourceVersion.RELEASE_11)
//@AutoService(Processor.class)
//public class MetaObjectCopyProcessor extends AbstractProcessor {
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        for (Element element : roundEnv.getElementsAnnotatedWith(ObjectCopy.class)) {
//            if (element.getKind() == ElementKind.METHOD) {
//                ExecutableElement method = (ExecutableElement) element;
//                processMethod(method);
//            }
//        }
//        return true;
//    }
//
//    private void processMethod(ExecutableElement method) {
//        try {
//            // 获取类名和方法信息
//            TypeElement classElement = (TypeElement) method.getEnclosingElement();
//            String className = classElement.getQualifiedName().toString();
//            String methodName = method.getSimpleName().toString();
//
//            // 正确获取方法参数类型
//            Type originAsmType = getAsmType(method.getParameters().get(0).asType());
//            Type targetAsmType = getAsmType(method.getParameters().get(1).asType());
//
//            // 正确获取方法描述符
//            String methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE, originAsmType, targetAsmType);
//
//            // 获取注解配置
//            ObjectCopy annotation = method.getAnnotation(ObjectCopy.class);
//            boolean copyNulls = annotation.copyNulls();
//            String[] ignoreFields = annotation.ignoreFields();
//            ObjectCopy.FieldMapping[] fieldMappings = annotation.fieldMappings();
//
//            // 读取原始类字节码
//            String binaryName = processingEnv.getElementUtils().getBinaryName(classElement).toString();
//            String relativePath = binaryName.replace('.', '/')+ ".class";
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "RelativePath:"+relativePath);
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Class:" + getClass().getName());
//
//
//            // 4. 使用 Filer 获取资源
//            Filer filer = processingEnv.getFiler();
//            FileObject fileObject = filer.getResource(
//                    StandardLocation.CLASS_OUTPUT, "", relativePath);
//
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Path:" + fileObject.toUri().toURL());
//            InputStream input = fileObject.openInputStream();
//            if (input == null) {
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Class file not found: " + binaryName);
//                return;
//            } else {
//
//            }
//            ClassReader cr = new ClassReader(input);
//            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
//
//            // 创建ClassVisitor修改方法
//            cr.accept(new ClassVisitor(Opcodes.ASM7, cw) {
//                @Override
//                public MethodVisitor visitMethod(int access, String name, String desc,
//                                                 String signature, String[] exceptions) {
//                    if (name.equals(methodName) && desc.equals(methodDesc)) {
//                        return new MethodVisitor(Opcodes.ASM7,
//                                super.visitMethod(access, name, desc, signature, exceptions)) {
//
//                            @Override
//                            public void visitCode() {
//                                // 完全替换方法体
//                                generateNewMethodBody(this, method, annotation);
//                                super.visitMaxs(0, 0); // 自动计算max stack/locals
//                            }
//
//                            @Override
//                            public void visitEnd() {
//                                // 不调用super，避免原始方法体被保留
//                            }
//                        };
//                    }
//                    return super.visitMethod(access, name, desc, signature, exceptions);
//                }
//            }, ClassReader.EXPAND_FRAMES);
//
//            // 将修改后的字节码写回文件系统
//            writeModifiedClass(className, cw.toByteArray());
//
//        } catch (Exception e) {
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
//                    "Failed to process @MetaObjectCopy: " + e.getMessage() + "\n" + sw.toString());
//        }
//    }
//
//    /**
//     * 根据完整类名获取 .class 文件路径
//     * @param className 完整类名（如 com.example.MyClass）
//     * @return .class 文件路径，如果找不到返回 null
//     */
//    public static String findClassFilePath(String className) {
//        try {
//            // 将类名转换为资源路径
//            String resourcePath = className.replace('.', '/') + ".class";
//            // 获取系统类加载器
//            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//            // 获取资源 URL
//            URL url = classLoader.getResource(resourcePath);
//            if (url != null) {
//                // 处理特殊字符和编码
//                String decodedPath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
//                // 处理 JAR 文件路径
//                if (decodedPath.contains("!")) {
//                    return decodedPath.substring(0, decodedPath.indexOf("!"));
//                }
//                return decodedPath;
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private void generateNewMethodBody(MethodVisitor mv, ExecutableElement method, ObjectCopy annotation) {
//        // 获取方法参数类型
//        TypeMirror originType = method.getParameters().get(0).asType();
//        TypeMirror targetType = method.getParameters().get(1).asType();
//
//        boolean copyNulls = annotation.copyNulls();
//        String[] ignoreFields = annotation.ignoreFields();
//        ObjectCopy.FieldMapping[] fieldMappings = annotation.fieldMappings();
//
//        // === 开始生成新方法体 ===
//        Label startLabel = new Label();
//        Label endLabel = new Label();
//        Label returnLabel = new Label();
//
//        mv.visitLabel(startLabel);
//
//        // 空值检查：if (origin == null || target == null) return;
//        mv.visitVarInsn(Opcodes.ALOAD, 1); // 加载origin
//        mv.visitJumpInsn(Opcodes.IFNULL, returnLabel);
//        mv.visitVarInsn(Opcodes.ALOAD, 2); // 加载target
//        mv.visitJumpInsn(Opcodes.IFNULL, returnLabel);
//
//        // 创建字段映射表
//        Map<String, String> fieldMap = new HashMap<>();
//        for (ObjectCopy.FieldMapping mapping : fieldMappings) {
//            fieldMap.put(mapping.target(), mapping.source());
//        }
//
//        // 获取目标类字段
//        TypeElement targetElement = (TypeElement) ((DeclaredType) targetType).asElement();
//        for (Element field : targetElement.getEnclosedElements()) {
//            if (field.getKind() != ElementKind.FIELD) continue;
//
//            String targetField = field.getSimpleName().toString();
//
//            // 检查忽略字段
//            if (Arrays.asList(ignoreFields).contains(targetField)) continue;
//
//            // 获取源字段名
//            String sourceField = fieldMap.getOrDefault(targetField, targetField);
//
//            // 生成字段拷贝逻辑
//            generateFieldCopy(mv, originType, targetType, sourceField, targetField,
//                    field.asType(), copyNulls);
//        }
//
//        mv.visitJumpInsn(Opcodes.GOTO, returnLabel);
//        mv.visitLabel(endLabel);
//
//        // 异常处理块（空实现）
//        mv.visitLabel(returnLabel);
//        mv.visitInsn(Opcodes.RETURN);
//
//        // 局部变量表（调试信息）
//        mv.visitLocalVariable("origin", Type.getDescriptor(Object.class), null, startLabel, endLabel, 1);
//        mv.visitLocalVariable("target", Type.getDescriptor(Object.class), null, startLabel, endLabel, 2);
//        mv.visitMaxs(3, 3); // 自动计算
//    }
//
//    private void generateFieldCopy(MethodVisitor mv, TypeMirror originType, TypeMirror targetType,
//                                   String sourceField, String targetField, TypeMirror fieldType,
//                                   boolean copyNulls) {
//        String getter = "get" + capitalize(sourceField);
//        String setter = "set" + capitalize(targetField);
//
//        // 使用新的类型转换方法
//        Type asmFieldType = getAsmType(fieldType);
//        String fieldDesc = asmFieldType.getDescriptor();
//
//        // 获取内部类名
//        String originInternal = getAsmType(originType).getInternalName();
//        String targetInternal = getAsmType(targetType).getInternalName();
//
//
//        Label afterSet = new Label();
//
//        // 加载target对象
//        mv.visitVarInsn(Opcodes.ALOAD, 2);
//
//        // 加载origin对象
//        mv.visitVarInsn(Opcodes.ALOAD, 1);
//
//        // 调用origin.getter()
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, originInternal, getter, "()" + fieldDesc, false);
//
//        // 空值检查
//        if (!copyNulls) {
//            mv.visitInsn(Opcodes.DUP); // 复制返回值
//            mv.visitJumpInsn(Opcodes.IFNULL, afterSet); // 如果为null则跳过setter
//        }
//
//        // 调用target.setter()
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, targetInternal, setter, "(" + fieldDesc + ")V", false);
//
//        if (!copyNulls) {
//            mv.visitLabel(afterSet);
//            mv.visitInsn(Opcodes.POP); // 弹出多余的null值
//        }
//    }
//
//    // 工具方法保持不变
//    private String capitalize(String s) {
//        return s.substring(0, 1).toUpperCase() + s.substring(1);
//    }
//
//    private String getInternalName(TypeMirror type) {
//        return getAsmType(type).getInternalName();
//    }
//
//    private String getTypeDescriptor(TypeMirror type) {
//        switch (type.getKind()) {
//            case BOOLEAN: return "Z";
//            case BYTE:    return "B";
//            case CHAR:    return "C";
//            case SHORT:   return "S";
//            case INT:     return "I";
//            case LONG:    return "J";
//            case FLOAT:   return "F";
//            case DOUBLE:  return "D";
//            case VOID:   return "V";
//            default:      return "L" + getInternalName(type) + ";";
//        }
//    }
//
//    private void writeModifiedClass(String className, byte[] bytecode) throws IOException {
//        String outputPath = processingEnv.getOptions().get("outputDirectory");
//        if (outputPath == null) {
//            outputPath = "out/production/classes"; // 默认输出目录
//        }
//
//        Path classFile = Paths.get(outputPath, className + ".class");
//        Files.createDirectories(classFile.getParent());
//        Files.write(classFile, bytecode, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//    }
//
//
//    private Type getAsmType(TypeMirror typeMirror) {
//        switch (typeMirror.getKind()) {
//            case BOOLEAN: return Type.BOOLEAN_TYPE;
//            case BYTE:    return Type.BYTE_TYPE;
//            case CHAR:    return Type.CHAR_TYPE;
//            case SHORT:   return Type.SHORT_TYPE;
//            case INT:    return Type.INT_TYPE;
//            case LONG:   return Type.LONG_TYPE;
//            case FLOAT:  return Type.FLOAT_TYPE;
//            case DOUBLE: return Type.DOUBLE_TYPE;
//            case VOID:   return Type.VOID_TYPE;
//            case ARRAY:
//                ArrayType arrayType = (ArrayType) typeMirror;
//                return Type.getType("[" + getAsmType(arrayType.getComponentType()).getDescriptor());
//            case DECLARED:
//                DeclaredType declaredType = (DeclaredType) typeMirror;
//                TypeElement typeElement = (TypeElement) declaredType.asElement();
//                return Type.getObjectType(typeElement.getQualifiedName().toString().replace('.', '/'));
//            default:
//                return Type.getType(Object.class);
//        }
//    }
//}
