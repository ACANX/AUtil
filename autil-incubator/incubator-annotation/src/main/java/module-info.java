module com.acanx.util.incubator.annotation {
    requires java.compiler; // 官方注解处理API
    requires jdk.compiler;
    requires com.squareup.javapoet;
    requires model.test; // 内部模块

}