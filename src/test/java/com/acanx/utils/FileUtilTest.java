package com.acanx.utils;

import com.acanx.constant.Constant;
import com.acanx.util.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUtilTest {

    // TODO
    private String content = "123456789099999999999999999999999999999999999999999999999999999999999999";
    // TODO
    private static String writeFilePath = "TestWrite.txt";

    /**
     *
     */
    @Test
    public void getFileMD5Test() {
        // 使用Java的系统属性来获取临时目录路径
        String tempDirPath = System.getProperty("java.io.tmpdir");
        String fullFilePath = tempDirPath + File.separator + "Java1" + File.separator + System.currentTimeMillis() + writeFilePath;
        File file = new File(fullFilePath);
        // 写入：
        try {
            FileUtil.write(file, content, Charset.forName(Constant.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String md5 = FileUtil.getFileMD5(file);
        System.out.println("File Path:" +fullFilePath);
        System.out.println("MD5 Hash: " + md5.toUpperCase());
        assertTrue(md5.toUpperCase().equals("04B1F0DA19A99756DC49DAC36E8F2526"));
    }

    /**
     *
     */
    @Test
    public void getFileSHA1Test() {
        // 使用Java的系统属性来获取临时目录路径
        String tempDirPath = System.getProperty("java.io.tmpdir");
        String fullFilePath = tempDirPath + File.separator + "Java2" + File.separator + System.currentTimeMillis() + writeFilePath;
        File file = new File(fullFilePath);
        // 写入：
        try {
            FileUtil.write(file, content, Charset.forName(Constant.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String sha1 = FileUtil.getFileSHA1(new File(fullFilePath));
        System.out.println("MFile Path:" +fullFilePath);
        System.out.println("SHA-1 Hash: " + sha1.toUpperCase());
        assertTrue(sha1.toUpperCase().equals("1AF7FECEAC8E593D0CDFAC043ABBE17B18B10711"));
    }


    /**
     *
     */
    @Test
    public void getFileWriteTest() {
        // 使用Java的系统属性来获取临时目录路径
        String tempDirPath = System.getProperty("java.io.tmpdir");
        System.out.println("System TempDirectory Path: " + tempDirPath);
        // 如果你想要一个File对象
        File tempDir = new File(tempDirPath);
        System.out.println("Java Temp Directory as File: " + tempDir.getAbsolutePath());

        String fullFilePath = tempDirPath + File.separator + "Java" + File.separator + System.currentTimeMillis() + writeFilePath;
        File file = new File(fullFilePath);
        // 输出找到的临时文件夹路径
        System.out.println("Java Test File Path: " + file.getAbsolutePath());


        System.out.println("Java Test Write Content: " + content);
        try {
            // 写入：
            FileUtil.write(file, content, Charset.forName(Constant.UTF_8));
            // 读取
            String str1 = FileUtil.readFileToString(file, Constant.UTF_8);
            String str2 = FileUtil.readFileAsString(file.getAbsolutePath());
            System.out.println("Java Test File Reader1 Content（FileUtil.readFileToString）: " + str1);
            System.out.println("Java Test File Reader2 Content（FileUtil.readFileAsString）: " + str2);
            assertTrue(content.equals(str1));
            assertTrue(content.equals(str2));
            assertTrue(str1.equals(str2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}