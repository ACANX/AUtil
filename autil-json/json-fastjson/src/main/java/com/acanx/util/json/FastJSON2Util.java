package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.filter.NameFilter;

import java.util.List;

/**
 * FastJSON2Util
 *
 */
@Alpha
public class FastJSON2Util {

    /**
     *
     *     对象转（小驼峰命名风格，紧凑型）的JSON字符串
     *
     * @param object  对象
     * @return    小下划线格式的JSON字符串
     */
    @Alpha
    public static String toJSONString(Object object){
        String jsonString = JSON.toJSONString(object);
        return jsonString;
    }


    /**
     *
     *     对象转（下划线命名风格，紧凑型）的JSON字符串
     *
     * @param object  对象
     * @return    小下划线格式的JSON字符串
     */
    @Alpha
    public static String toJSONStringSnake(Object object){
        JSONWriter.Context context = new JSONWriter.Context();
        NameFilter nf= NameFilter.of(PropertyNamingStrategy.SnakeCase);
        context.setNameFilter(nf);
        String jsonString = JSON.toJSONString(object, nf);
        return jsonString;
    }

    /**
     *
     *     对象转（下划线命名风格、化过格式化输出展示）的JSON字符串
     *
     * @param object 对象
     * @return  下划线格式的美化过格式化输出展示的JSON字符串
     */
    @Alpha
    public static String toJSONStringPrettyFormat(Object object){
        JSONWriter.Context context = new JSONWriter.Context();
        NameFilter nf= NameFilter.of(PropertyNamingStrategy.SnakeCase);
        context.setNameFilter(nf);
        String jsonString = JSON.toJSONString(object, nf, JSONWriter.Feature.PrettyFormat);
        return jsonString;
    }


    /**
     *    对象转小驼峰风格的JSON字符串
     *
     * @param object  对象
     * @return        小驼峰格式的JSON字符串
     */
    @Alpha
    public static String toJSONStringForStorage(Object object){
        String jsonString = JSON.toJSONString(object);
        return jsonString;
    }

    /**
     * Java对象序列化为JSON字符串(大对象)
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    public static String toJSONStringLarge(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.LargeObject);
    }


    /**
     *   JSON字符串 转Java对象
     *
     * @param text   JSON字符串
     * @param objectClass  对象类型
     * @return       对象
     * @param <T>    对象类型
     */
    @Alpha
    public static <T> T parseObject(String text, Class<T> objectClass) {
        return JSON.parseObject(text, objectClass, JSONReader.Feature.SupportSmartMatch);
    }




    /**
     *   JSON字符串 转List集合
     *
     * @param text       JSON字符串
     * @param objectClass   对象类型
     * @return          集合
     * @param <T>         集合元素类型
     */
    @Alpha
    public static <T> List<T> parseArray(String text, Class<T> objectClass) {
        return JSON.parseArray(text, objectClass, JSONReader.Feature.SupportSmartMatch);
    }





}
