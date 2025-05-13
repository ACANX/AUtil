package com.acanx.util.properties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PropertiesFileTest {

    public static void main(String[] args) {
        OrderedProperties props = new OrderedProperties();
        // 加载文件
        try (InputStream in = PropertiesFileTest.class.getClassLoader().getResourceAsStream("properties/config.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 修改属性
        props.setProperty("descriptor.comment", "newValue");
        props.setProperty("comment", "newValue");
        // 添加新属性
        props.setProperty("new.key", "value");
        props.setProperty("new.aa", "AA");
        // 保存文件
        try (OutputStream out = new FileOutputStream("D:\\Code\\JavaCode\\acanx-utils\\src\\test\\resources\\properties\\updated.properties")) {
            props.store(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 按键排序并保存到新文件
        try{
            File file = new File("D:\\Code\\JavaCode\\acanx-utils\\src\\test\\resources\\properties\\sorted.properties");
            props.resortAndStore(file); // 生成排序后的文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
