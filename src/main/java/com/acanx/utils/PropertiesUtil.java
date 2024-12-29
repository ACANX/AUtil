package com.acanx.utils;

import com.acanx.annotation.Alpha;
import com.acanx.common.model.PropertyItem;
import com.acanx.constant.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * ACANX-Util / com.acanx.utils / PropertiesUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:54
 *
 *  PropertiesUtil:
 * 补充说明：
 *  2019/1/5  15:54
 *
 * @author ACANX
 * @since 0.0.1.10
 */
@Alpha
public class PropertiesUtil {

    private static Properties loadConfig(File file) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }
        return properties;
    }

    public static String getPropertiesFileConfigValue(File file, String key) {
        Properties properties = loadConfig(file);
        return properties.getProperty(key);
    }

    private static String getPropertiesFileConfigValue(Properties properties, String key) {
        return properties.getProperty(key);
    }

    /**
     *  修改配置，（注意：此操作会修改配置项的原有顺序，如需要保证顺序请选择 @See modifyProperties ）
     * @param file
     * @param key
     * @param value
     */
    public static void setPropertiesFileConfigValue(File file, String key, String value) {
        Properties properties = loadConfig(file);
        properties.setProperty(key, value);
        saveConfig(properties, file);
    }

    private static void saveConfig(Properties properties, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, "");
        } catch (IOException e) {
            System.err.println("Error saving properties: " + e.getMessage());
        }
    }


    /**
     *  修改配置文件的配置值，不改变配置的顺序。
     * @param file
     * @param key
     * @param value
     */
    public static void modifyProperties(File file, String key, String value) {
        if (StringUtil.isNotBlank(key) && StringUtil.isNotBlank(value) && null != getPropertiesFileConfigValue(file, key)){
            Map<String, PropertyItem> configMap = new HashMap<String, PropertyItem>();
            List<String> configList;
            try {
                configList = FileUtil.readLines(file, Constant.UTF_8);
                for (int i = 0; i < configList.size(); i++) {
                    String line = configList.get(i);
                    String[] keyValue = line.split("=");
                    if (StringUtil.isNotBlank(line) && !line.startsWith("#") && keyValue.length == 2) {
                        PropertyItem item = new PropertyItem(keyValue[0], keyValue[1], i);
                        configMap.put(keyValue[0], item);
                    }
                }
            } catch (IOException e) {
                // TODO
                throw new RuntimeException(e);
            }
            PropertyItem qItem = configMap.get(key);
            if(null != qItem) {
                // 存在
                String newValue = qItem.getKey() + "=" + value;
                qItem.setValue(value);
                configMap.put(key, qItem);
                configList.set(qItem.getLine(), newValue);
            } else {
                // 不存在 追加
                String newValue = key + "=" + value;
                configList.add(newValue);
            }
            try {
                FileUtil.writeLines(file, configList, Charset.forName("UTF-8"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     *   一次完成修改多项配置后保存到文件
     *
     * @param file
     * @param needModPropMap
     */
    public static void modifyProperties(File file, Map<String, String> needModPropMap, boolean allowAddProp) {
        // 构建配置文件数据结构
        Map<String, PropertyItem> propMap = new HashMap<String, PropertyItem>();
        List<String> propList;
        List<String> montagedPropList = new ArrayList<>();
        try {
            propList = FileUtil.readLines(file, Constant.UTF_8);

            int lineCount = 1;
            String montageStr = "";
            for (int i = 0; i < propList.size(); i++) {
                String currLine = propList.get(i);
                if (currLine.trim().endsWith("\\")){
                    montageStr = montageStr + currLine;
                    lineCount ++;
                } else {
                    if (lineCount > 1){
                        montagedPropList.add(montageStr + currLine);
                    } else {
                        montagedPropList.add(currLine);
                    }
                    montageStr = "";
                    lineCount = 1;
                }

            }

            // 将处理完的List中的key：value及行号记录到propMap中，用于下面的修改配置项判断
            for (int i = 0; i < montagedPropList.size(); i++) {
                String line = montagedPropList.get(i);
                String[] keyValue = line.split("=");
                if (StringUtil.isNotBlank(line) && !line.startsWith("#") && keyValue.length == 2) {
                    PropertyItem item = new PropertyItem(keyValue[0], keyValue[1], i);
                    propMap.put(keyValue[0], item);
                }
            }
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }

        // 修改对应的配置项
        for (String key : needModPropMap.keySet()) {
            String value = needModPropMap.get(key);
            if (StringUtil.isNotBlank(key) && StringUtil.isNotBlank(value)) {
                PropertyItem pItem = propMap.get(key);
                if (null != pItem) {
                    // 存在 修改
                    String newLine = pItem.getKey() + "=" + value;
                    pItem.setValue(value);
                    propMap.put(key, pItem);
                    montagedPropList.set(pItem.getLine(), newLine);
                } else if (allowAddProp){
                    // 不存在 追加到文件最后面
                    String newLine = key + "=" + value;
                    montagedPropList.add(newLine);
                    pItem = new PropertyItem(key, value, montagedPropList.size() - 1);
                    propMap.put(key, pItem);
                }
            }
        }
        // 保存配置到文件
        try {
            FileUtil.writeLines(file, montagedPropList, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
