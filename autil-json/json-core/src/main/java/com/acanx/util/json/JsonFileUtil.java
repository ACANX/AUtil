package com.acanx.util.json;

import com.acanx.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JsonFileUtil   JSON文件工具类
 *
 * @author ACANX
 * @since 20251105
 */
public class JsonFileUtil {



    /**
     *   将本地的JSON文件直接序列化为JSON集合
     *
     * @param filePath  文件路径
     * @param objectClass  反序列化的类型
     * @return            集合
     * @param <T>      类型
     */
    public static <T> List<T> parseArrayFromLocalPath(String filePath, Class<T> objectClass) {
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
     * @param filePath  文件路径
     * @param list      集合对象
     */
    public static void writeArrayToLocalFile(String filePath, List<?> list) {
        try {
            String content = JSONUtil.toJSONStringPrettyFormat(list);
            FileUtil.write(new File(filePath), content);
            System.out.println("数据写入文件Path: " + filePath);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }


    /**
     * 通用的List转Map方法
     *
     * @param list     集合
     * @param keyExtractor    处理函数
     * @return
     * @param <T>      list集合中的元素类型
     * @param <K>     从元素中提取key的函数
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
    public static <T, K> Map<K, T> parseJsonArrayToMap(
            String filePath,
            Class<T> elementType,
            Function<T, K> keyExtractor) {
        List<T> list = parseArrayFromLocalPath(filePath, elementType);
        return listToMap(list, keyExtractor);
    }



}
