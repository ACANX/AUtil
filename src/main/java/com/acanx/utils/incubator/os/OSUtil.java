package com.acanx.utils.incubator.os;

import com.acanx.annotation.Alpha;

/**
 *  操作系统工具类
 *
 * @since 0.0.1.10
 */
@Alpha
public class OSUtil {

    public static String getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "Windows";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return "Linux";
        } else if (osName.contains("mac")) {
            return "MacOS";
        } else if (osName.contains("android")) {
            return "Android";
        } else if (osName.contains("darwin") && (System.getProperty("java.vendor").toLowerCase().contains("apple"))) {
            // This is to distinguish macOS running Java from iOS simulators or devices,
            // though typically iOS devices won't run standard Java applications directly.
            // This is more for completeness.
            return "MacOS"; // In case of a Java runtime on iOS (uncommon), it would need specific handling.
        } else if (System.getProperty("java.vendor").toLowerCase().contains("apple")) {
            // This part is more speculative for detecting iOS-like environments
            // but typically Java doesn't run directly on iOS, so this is more for completeness.
            return "iOS";
        } else {
            return "Undefined";
        }
    }

    public static boolean isWindowsOS(){
        return getOSType().equalsIgnoreCase("Windows");
    }

    public static boolean isAndroidOS(){
        return getOSType().equalsIgnoreCase("Android");
    }

    public static boolean isLinuxOS(){
        return getOSType().equalsIgnoreCase("Linux");
    }

    public static boolean isMacOS(){
        return getOSType().equalsIgnoreCase("MacOS");
    }

}
