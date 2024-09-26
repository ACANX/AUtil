package com.acanx.utils.incubator;

import com.acanx.utils.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *  FileZipUtil
 *
 * @since 0.0.1.10
 */
public class FileZipUtil {


    /**
     *  压缩文件
     *
     * @param zipOutputStream 要锁输出流
     * @param file 文件
     * @param parentFileName  父级目录
     */
    private static void zipFile(ZipOutputStream zipOutputStream, File file, String parentFileName) {
        FileInputStream in = null;

        try {
            ZipEntry zipEntry = new ZipEntry(parentFileName + file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            in = new FileInputStream(file);
            byte[] buf = new byte[8192];

            int len;
            while((len = in.read(buf)) != -1) {
                zipOutputStream.write(buf, 0, len);
            }

            zipOutputStream.closeEntry();
        } catch (FileNotFoundException var17) {
            FileNotFoundException e = var17;
            e.printStackTrace();
        } catch (Exception var18) {
            Exception e = var18;
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException var16) {
                IOException e = var16;
                e.printStackTrace();
            }

        }

    }

    /**
     *  directory
     * @param zipOutputStream 要锁输出流
     * @param file  文件
     * @param parentFileName 父级目录
     */
    private static void directory(ZipOutputStream zipOutputStream, File file, String parentFileName) {
        File[] files = file.listFiles();
        String parentFileNameTemp = null;
        File[] var5 = files;
        int var6 = files.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            File fileTemp = var5[var7];
            parentFileNameTemp = StringUtil.isEmpty(parentFileName) ? fileTemp.getName() : parentFileName + "/" + fileTemp.getName();
            if (fileTemp.isDirectory()) {
                directory(zipOutputStream, fileTemp, parentFileNameTemp);
            } else {
                zipFile(zipOutputStream, fileTemp, parentFileNameTemp);
            }
        }

    }

    /**
     *  压缩文件
     *
     * @param source 源路径
     * @param target 输出目标文件路径
     */
    public static void zipFiles(String source, String target) {
        File file = new File(source);
        ZipOutputStream zipOutputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(target);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            if (file.isDirectory()) {
                directory(zipOutputStream, file, "");
            } else {
                zipFile(zipOutputStream, file, "");
            }
        } catch (Exception var14) {
            Exception e = var14;
            e.printStackTrace();
        } finally {
            try {
                zipOutputStream.close();
                fileOutputStream.close();
            } catch (IOException var13) {
                IOException e = var13;
                e.printStackTrace();
            }

        }

    }




}
