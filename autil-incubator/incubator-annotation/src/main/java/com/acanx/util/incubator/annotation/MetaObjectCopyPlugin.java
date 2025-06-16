package com.acanx.util.incubator.annotation;//package com.acanx.util.object.copy;
//
//import com.sun.source.util.JavacTask;
//import com.sun.source.util.Plugin;
//import com.sun.source.util.TaskEvent;
//import com.sun.source.util.TaskListener;
//
//import javax.annotation.processing.SupportedAnnotationTypes;
//import javax.annotation.processing.SupportedSourceVersion;
//import javax.lang.model.SourceVersion;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//
//import com.sun.tools.javac.api.BasicJavacTask;
//import com.sun.tools.javac.model.JavacElements;
//import com.sun.tools.javac.tree.JCTree;
//import com.sun.tools.javac.tree.TreeMaker;
//import com.sun.tools.javac.util.Context;
//import com.sun.tools.javac.util.Names;
//
//
///**
// * MetaObjectCopyPlugin
// *
// * @author ACANX
// * @since 202506
// */
//
//@SupportedAnnotationTypes("MetaObjectCopy")
//@SupportedSourceVersion(SourceVersion.RELEASE_11)
//public class MetaObjectCopyPlugin implements Plugin {
//
//    static {
//        System.out.println("=== 插件初始化开始 ===");
//        String serviceFile = "META-INF/services/com.sun.source.util.Plugin";
//        try (InputStream is = MetaObjectCopyPlugin.class.getClassLoader()
//                .getResourceAsStream(serviceFile)) {
//            if (is == null) {
//                System.err.println("❌ 服务文件未找到: " + serviceFile);
//            } else {
//                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
//                System.out.println("✅ 服务文件内容: " + content);
//
//                if (!content.contains("com.acanx.util.object.copy.MetaObjectCopyPlugin")) {
//                    System.err.println("❌ 服务文件不包含本插件类名");
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("❌ 读取服务文件失败: " + e.getMessage());
//        }
//        System.out.println("=== 插件初始化结束 ===");
//    }
//
//
//
//    @Override
//    public String getName() {
//        return "MetaObjectCopy";
//    }
//
//    @Override
//    public void init(JavacTask task, String... args) {
//        // JDK 版本检测
//        String javaVersion = System.getProperty("java.version");
//        Context context = ((BasicJavacTask) task).getContext();
//        JavacElements elements = JavacElements.instance(context);
//        TreeMaker maker = TreeMaker.instance(context);
//        Names names = Names.instance(context);
//
//        task.addTaskListener(new TaskListener() {
//            @Override
//            public void started(TaskEvent e) {}
//
//            @Override
//            public void finished(TaskEvent e) {
//                if (e.getKind() == TaskEvent.Kind.ANALYZE) {
//                    // JDK 11 兼容的扫描方式
//                    JCTree.JCCompilationUnit compUnit = (JCTree.JCCompilationUnit) e.getCompilationUnit();
//
//                    if (javaVersion.startsWith("1.8")) {
//                        // JDK 8 兼容方式 (使用已添加的 scan(JCTree, Void) 方法)
////                        new ASTScanner(maker, elements, names).scan(compUnit, null);
//                    } else {
//                        // JDK 11+ 标准方式
////                        new ASTScanner(maker, elements, names).scan(compUnit);
//                    }
//                }
//            }
//        });
//    }
//
//
//}
