package com.acanx.util.incubator;

import com.acanx.annotation.Alpha;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *  FileZipUtil
 *
 * @since 0.0.1.10
 */
@Alpha
public class ZipUtil {

    /**
     *   压缩单个文件到 ZIP文件中
     *
     * @param sourceFilePath   源文件夹路径（如 "/target/test-classes/properties/config.properties"）
     * @param outputZipPath   输出 ZIP 文件路径（如 "/target/test-classes/result.zip"）
     * @throws IOException    IOD异常
     */
    @Alpha
    public static void zipFile(String sourceFilePath, String outputZipPath) throws IOException {
        Path sourceFile = Paths.get(sourceFilePath);
        String fileName = sourceFile.getFileName().toString();
        Path outputZip = Paths.get(outputZipPath);
        if (!Files.exists(outputZip)) {
            Files.createFile(outputZip);
        }
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZipPath))) {
            String relativePath = fileName.replace("\\", "/");
            System.out.println("AddFileToZip:" + relativePath);
            zos.putNextEntry(new ZipEntry(relativePath));
            Files.copy(sourceFile, zos);
            zos.closeEntry();
        }
    }



    /**
     *   压缩文件夹到 ZIP 文件中
     *
     * @param sourceDirPath   源文件夹路径（如 "/target/test-classes/properties"）
     * @param outputZipPath   输出 ZIP 文件路径（如 "/target/test-classes/result.zip"）
     * @throws IOException    IOD异常
     */
    @Alpha
    public static void zipDirectory(String sourceDirPath, String outputZipPath) throws IOException {
        Path sourceDir = Paths.get(sourceDirPath);
        Path outputZip = Paths.get(outputZipPath);
        if (!Files.exists(outputZip)) {
            Files.createFile(outputZip);
        }
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZipPath))) {
            // 遍历文件夹并写入 ZIP
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    addFileToZip(file, zos, sourceDir);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // 跳过根目录（不将根目录自身加入 ZIP）
                    if (!dir.equals(sourceDir)) {
                        addDirToZip(dir, zos, sourceDir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     *   添加文件到 ZIP
     *
     * @param file  文件
     * @param zos   压缩流
     * @param sourceDir  源文件夹
     * @throws IOException   IO异常
     */
    private static void addFileToZip(Path file, ZipOutputStream zos, Path sourceDir) throws IOException {
        String relativePath = sourceDir.relativize(file).toString().replace("\\", "/");
        System.out.println("AddFileToZip:" + relativePath);
        zos.putNextEntry(new ZipEntry(relativePath));
        Files.copy(file, zos);
        zos.closeEntry();
    }

    /**
     *   添加空目录到 ZIP
     *
     * @param dir           目录
     * @param zos           压缩文件输出流
     * @param sourceDir     源文件夹
     * @throws IOException  IO异常
     */
    private static void addDirToZip(Path dir, ZipOutputStream zos, Path sourceDir) throws IOException {
        String relativePath = sourceDir.relativize(dir).toString().replace("\\", "/") + "/";
        System.out.println("AddDirToZip:" + relativePath);
        zos.putNextEntry(new ZipEntry(relativePath));
        zos.closeEntry();
    }




}
