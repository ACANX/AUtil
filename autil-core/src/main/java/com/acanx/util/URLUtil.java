package com.acanx.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 *   URLUtil
 *
 */
public class URLUtil {


    /**
     *  编码字符串参数
     *
     * @param value  原字符串
     * @return       编码后的字符串
     */
    public static String encodeParameter(String value) {
        // 显式使用 UTF-8 编码
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 如果需要保持 JDK 8 的空格编码行为
        return encoded.replace("%20", "+");
    }

    /**
     *  编码URL参数
     *
     * @param entry  Map Entry
     * @return    编码后的字符串
     */
    public static String encodeParameter(Map.Entry<String, String> entry) {
        return encodeParameter(entry.getKey()) + "=" + encodeParameter(entry.getValue());
    }


    /**
     *    编码URL参数
     *
     * @param key  键
     * @param value 值
     * @return   编码后的字符串
     */
    public static String encodeParameter(String key, String value) {
        return encodeParameter(key) + "=" + encodeParameter(value);
    }



    /**
     *    从互联网下载照片
     *
     * @param imageUrl     图片链接
     * @param destinationPath   图片本地保存地址
     * @return    下载结果
     */
    public static boolean downloadImage(String imageUrl, String destinationPath) {
        File file = new File(destinationPath);
        if (!file.exists()){
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.err.println("下载【"+ imageUrl + "】发生IOException:" +  e.getMessage());
            return false;
        }
        System.out.println(String.format("文件[%s]下载完成！保存在[%s]", imageUrl, destinationPath));
        return true;
    }

}
