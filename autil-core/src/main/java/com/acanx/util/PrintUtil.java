package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.util.Map;
import java.util.TreeMap;

/**
 * PrintUtil  打印工具类
 *
 * @author ACANX
 * @since 0.2.2.0
 */
@Alpha
public class PrintUtil {

    /**
     * 支持打印运行时环境变量信息
     *
     * @since 0.2.2.0
     */
    public static void printCfRuntimeEnvInfo() {
        // 获取环境变量并排序
        Map<String, String> env = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        env.putAll(System.getenv());
        // 计算最长变量名用于格式化
        int maxKeyLength = env.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(20);  // 默认最小宽度
        // 打印表头
        System.out.println("+-" + "-".repeat(maxKeyLength) + "-+-" + "-".repeat(90) + "-+");
        System.out.printf("| %-" + maxKeyLength + "s | %-90s |\n", "环境变量", "值");
        System.out.println("+=" + "=".repeat(maxKeyLength) + "=+=" + "=".repeat(90) + "=+");
        // 打印环境变量
        env.forEach((key, value) -> {
            // 处理长值（截断并添加省略号）
            String displayValue = value;
            if (value.length() > 90) {
                displayValue = value.substring(0, 87) + "...";
            }
            // 处理多行值（只显示第一行）
            if (displayValue.contains("\n")) {
                displayValue = displayValue.split("\n")[0] + " ...";
            }
            System.out.printf("| %-" + maxKeyLength + "s | %-90s |\n", key, displayValue);
        });
        // 打印表尾
        System.out.println("+-" + "-".repeat(maxKeyLength) + "-+-" + "-".repeat(90) + "-+");
        // 添加统计信息
        System.out.println("环境变量总数: " + env.size());
        System.out.println("Java 版本: " + System.getProperty("java.version"));
        System.out.println("操作系统: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }

}
