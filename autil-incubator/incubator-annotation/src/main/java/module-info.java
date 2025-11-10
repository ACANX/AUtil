module com.acanx.util.incubator.annotation {


    requires java.compiler;
    requires static jdk.compiler;
    requires model.test;


    // 导出注解处理器所在的包
    exports com.acanx.util.incubator.annotation;
    exports com.acanx.util.incubator.annotation.ann;
    // 关键修正：打开包到jdk.compiler模块 允许反射访问
    opens com.acanx.util.incubator.annotation.ann to jdk.compiler;

    provides javax.annotation.processing.Processor
            with com.acanx.util.incubator.annotation.ann.ObjectCopierProcessor;
}