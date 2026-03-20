package com.acanx.util.json;

import com.acanx.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JSONFileUtil
 *
 * @author ACANX
 * @since 20260122
 */
public class JacksonFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(JacksonFileUtil.class);

    /**
     *   将本地的JSON文件直接序列化为JSON集合
     *
     * @param filePath  文件路径
     * @param objectClass  反序列化的类型
     * @return            集合
     * @param <T>      类型
     */
    public static <T> List<T> parseArray(String filePath, Class<T> objectClass) {
        String content = null;
        try {
            content = FileUtil.readFileAsString(filePath);
            return JSONUtil.parseArray(content, objectClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *   数组数据写入文件
     *
     * @param filePath
     * @param list
     */
    public static void writeArrayToLocalFile(String filePath, Object list) {
        try {
            String content = JSONUtil.toJSONStringPrettyFormat(list);
            FileUtil.write(new File(filePath), content);
            logger.info("数据写入文件Path:{}", filePath);
        } catch (IOException e) {
            logger.error("IOException:{}", e.getMessage());
        }
    }


    /**
     * 通用的List转Map方法
     */
    public static <T, K> Map<K, T> listToMap(List<T> list, Function<T, K> keyExtractor) {
        if (list == null) {
            return new HashMap<>();
        }
        Map<K, T> result = new HashMap<>();
        for (T element : list) {
            K key = keyExtractor.apply(element);
            if (key != null) {
                // 如果出现重复key，后面的会覆盖前面的
                result.put(key, element);
            }
        }
        return result;
    }

    /**
     * 将JSON文件中的数组转换为HashMap（使用默认ObjectMapper）
     *
     * @param filePath JSON文件路径
     * @param elementType 数组元素类型
     * @param keyExtractor 从元素中提取key的函数
     * @return 转换后的HashMap
     */
    public static <T, K> Map<K, T> parseArrayToMap(
            String filePath,
            Class<T> elementType,
            Function<T, K> keyExtractor) {
        List<T> list = parseArray(filePath, elementType);
        return listToMap(list, keyExtractor);
    }


    /**
     * 在文件系统目录中递归查找JSON文件（用于开发环境）
     */
    public static void findJsonFilesInDirectory(File directory, String basePath, List<String> jsonFiles) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        findJsonFilesInDirectory(file, basePath, jsonFiles);
                    } else if (file.getName().endsWith(".json")) {
                        // 转换为相对于basePath的路径
                        String relativePath = file.getPath()
                                .replace(File.separator, "/")
                                .replaceFirst(".*" + basePath + "/?", "");
                        jsonFiles.add(basePath + "/" + relativePath);
                    }
                }
            }
        }
    }


    public static File writeContentToLocalTempFile(String prefix, Object ro){
        String path = "/tmp/TempFile" +  prefix + System.nanoTime()+".json";
        File file = new File(path);
        try {
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            FileUtil.write(file, JacksonUtil.toJSONString(ro), StandardCharsets.UTF_8);
            logger.debug("数据写入临时文件[{}]操作完成！", file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }


}
