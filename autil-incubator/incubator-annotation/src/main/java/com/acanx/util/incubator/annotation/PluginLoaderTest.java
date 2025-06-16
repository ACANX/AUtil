package com.acanx.util.incubator.annotation;//package com.acanx.util.object.copy;
//
//import com.sun.source.util.Plugin;
//
//import java.util.ServiceLoader;
//
///**
// * PluginLoaderTest
// *
// * @author ACANX
// * @date 2025-06-14
// * @since 202506
// */
//public class PluginLoaderTest {
//    public static void main(String[] args) {
//        System.out.println("=== 开始加载插件 ===");
//        System.out.println("类路径: " + System.getProperty("java.class.path"));
//
//        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
//        int count = 0;
//
//        for (Plugin plugin : loader) {
//            System.out.println("✅ 找到插件: " + plugin.getClass().getName());
//            System.out.println("   插件名称: " + plugin.getName());
//            count++;
//        }
//
//        if (count == 0) {
//            System.err.println("❌ 未找到任何插件");
//
//            // 尝试直接加载类
//            try {
//                Class<?> clazz = Class.forName("com.acanx.util.object.copy.MetaObjectCopyPlugin");
//                System.out.println("ℹ️ 类存在: " + clazz);
//
//                // 尝试实例化
//                Plugin plugin = (Plugin) clazz.getDeclaredConstructor().newInstance();
//                System.out.println("✅ 手动创建插件成功: " + plugin.getName());
//            } catch (ClassNotFoundException e) {
//                System.err.println("❌ 类未找到: com.acanx.util.object.copy.MetaObjectCopyPlugin");
//            } catch (Exception e) {
//                System.err.println("❌ 创建实例失败: " + e.getMessage());
//            }
//        }
//    }
//}
