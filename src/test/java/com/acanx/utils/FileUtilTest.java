package com.acanx.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

public class FileUtilTest {

    // TODO
    private static String filePath = "C:\\Users\\ACANX\\Downloads\\请Readme.PNG";

    @Test
    public void getFileMD5Test() {
        String md5 = FileUtil.getFileMD5(new File(filePath));
        System.out.println("File Path:" +filePath);
        System.out.println("MD5 Hash: " + md5.toUpperCase());
    }

    @Test
    public void getFileSHA1Test() {
        String sha1 = FileUtil.getFileSHA1(new File(filePath));
        System.out.println("MFile Path:" +filePath);
        System.out.println("SHA-1 Hash: " + sha1.toUpperCase());
    }
}