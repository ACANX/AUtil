package com.acanx.util.hardware;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * CpuInfo
 *
 * @author ACANX
 * @since 20250709
 */
public class CpuInfo {

    public static int getPhysicalCores() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                return getWindowsPhysicalCores();
            } else if (os.contains("linux")) {
                return getLinuxPhysicalCores();
            } else if (os.contains("mac")) {
                return getMacPhysicalCores();
            } else {
                // 其他系统回退到逻辑核心数
                return Runtime.getRuntime().availableProcessors();
            }
        } catch (Exception e) {
            // 异常时返回逻辑核心数
            return Runtime.getRuntime().availableProcessors();
        }
    }

    private static int getWindowsPhysicalCores() throws Exception {
        Process process = Runtime.getRuntime().exec("wmic cpu get NumberOfCores");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("\\d+")) {
                    return Integer.parseInt(line.trim());
                }
            }
        }
        throw new Exception("Physical core count not found");
    }

    private static int getLinuxPhysicalCores() throws Exception {
        Process process = Runtime.getRuntime().exec("lscpu -p | egrep -v '^#' | sort -u -t, -k 2,4 | wc -l");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String output = reader.readLine().trim();
            return Integer.parseInt(output);
        }
    }

    private static int getMacPhysicalCores() throws Exception {
        Process process = Runtime.getRuntime().exec("sysctl -n hw.physicalcpu");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String output = reader.readLine().trim();
            return Integer.parseInt(output);
        }
    }
}
