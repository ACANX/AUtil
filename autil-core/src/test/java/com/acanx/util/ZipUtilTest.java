package com.acanx.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


class ZipUtilTest {

    @Test
    void zipFiles() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpDir);
        String dir = tmpDir + File.separator + "Zip";
        File zipDir = new File(dir);
        if (!zipDir.exists()) {
            zipDir.mkdirs();
        }
        String savePath = tmpDir + "Zip"+ File.separator + "zipFiles" + System.currentTimeMillis()+".zip";
        System.out.println(savePath);
        try {
            URL url = ZipUtilTest.class.getProtectionDomain().getCodeSource().getLocation();
            String path = url.getPath().substring(1)+ "properties/config.properties";
            System.out.println(path);
            ZipUtil.zipFile(path, savePath);
            System.out.println("压缩成功！");
        } catch (FileNotFoundException e) {
            System.err.println("压缩失败: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File(savePath);
        Assertions.assertTrue(file.exists());
    }

    @Test
    void zipFolderFiles() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpDir);
        String savePath = tmpDir + "Zip"+ File.separator + "custom_name" + System.currentTimeMillis()+".zip";
        System.out.println(savePath);
        try {
            InputStream is = ZipUtilTest.class.getResourceAsStream("/properties");
            URL url = ZipUtilTest.class.getProtectionDomain().getCodeSource().getLocation();
            String path = url.getPath().substring(1)+ "properties";
            System.out.println(path);
            ZipUtil.zipDirectory(path, savePath);
            System.out.println("压缩成功！");
        } catch (IOException e) {
            System.err.println("压缩失败: " + e.getMessage());
        }
        File file = new File(savePath);
        Assertions.assertTrue(file.exists());
    }
}