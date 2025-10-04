package com.acanx.util.os;

import com.acanx.annotation.Alpha;

/**
 *  操作系统工具类
 *
 * @since 0.0.1.10
 */
@Alpha
public class OSUtil {

        /**
     * 获取当前操作系统的类型
     *
     * 该函数通过读取系统属性来判断当前运行的操作系统类型，
     * 支持识别Windows、Linux、MacOS、Android、iOS等操作系统。
     *
     * @return String 返回操作系统类型字符串，可能的值包括：
     *         - "Windows"：Windows操作系统
     *         - "Linux"：Linux系列操作系统（包括Unix、AIX等）
     *         - "MacOS"：MacOS操作系统
     *         - "Android"：Android操作系统
     *         - "iOS"：iOS操作系统（推测性识别）
     *         - "Undefined"：无法识别的操作系统类型
     */
    public static String getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();

        // 根据操作系统名称判断系统类型
        if (osName.contains("win")) {
            return "Windows";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return "Linux";
        } else if (osName.contains("mac")) {
            return "MacOS";
        } else if (osName.contains("android")) {
            return "Android";
        } else if (osName.contains("darwin") && (System.getProperty("java.vendor").toLowerCase().contains("apple"))) {
            // 区分运行Java的macOS与iOS模拟器或设备
            // 通常iOS设备不会直接运行标准Java应用程序，此处更多是为了完整性考虑
            // 如果在iOS上运行Java运行时（不常见），则需要特殊处理
            return "MacOS";
        } else if (System.getProperty("java.vendor").toLowerCase().contains("apple")) {
            // 这部分代码用于推测性地检测iOS类环境
            // 但通常Java不会直接在iOS上运行，所以更多是为了完整性考虑
            return "iOS";
        } else {
            return "Undefined";
        }
    }


        /**
     * 判断当前操作系统是否为Windows系统
     *
     * @return true表示当前操作系统是Windows，false表示不是Windows系统
     */
    public static boolean isWindowsOS(){
        return getOSType().equalsIgnoreCase("Windows");
    }


        /**
     * 判断当前操作系统是否为Android系统
     *
     * @return boolean - 如果当前操作系统是Android则返回true，否则返回false
     */
    public static boolean isAndroidOS(){
        return getOSType().equalsIgnoreCase("Android");
    }


        /**
     * 判断当前操作系统是否为Linux系统
     *
     * @return boolean 返回true表示当前操作系统是Linux，返回false表示不是Linux系统
     */
    public static boolean isLinuxOS(){
        return getOSType().equalsIgnoreCase("Linux");
    }


        /**
     * 判断当前操作系统是否为MacOS
     *
     * @return boolean 返回true表示当前操作系统是MacOS，否则返回false
     */
    public static boolean isMacOS(){
        return getOSType().equalsIgnoreCase("MacOS");
    }


}
