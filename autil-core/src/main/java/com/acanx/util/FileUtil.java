package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * ACANX-Util / com.acanx.utils / FileUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:47
 *  FileUtil:
 * 补充说明：
 *  2019/1/5  15:47
 *
 *
 * @author ACANX
 * @since 0.0.1
 */
public class FileUtil {
   private static final Logger logger = Logger.getLogger(FileUtil.class.getName());

    private static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;

    /**
     * 构造函数
     * @hidden
     */
    private FileUtil() {
    }

    /**
     *  获取系统临时目录
     *
     * @return 系统临时目录路径字符串
     */
    public static String getSysTempDir(){
        return System.getProperty("java.io.tmpdir");
    }

    /**
     *   文件是否存在
     *
     * @param file 文件
     * @return     判断结果
     */
    @Alpha
    public static boolean fileExists(File file) {
        if (file.isFile() && file.exists() && file.length() > 0) {
            return true;
        }
        return false;
    }


    /**
     * 移动文件
     *
     * @param src  源文件
     * @param dest 目标文件
     * @throws IOException 如果移动文件时发生错误
     */
    @Alpha
    public static void moveFile(File src, File dest) throws IOException {
        // 将File对象转换为Path对象
        Path sourcePath = src.toPath();
        Path destinationPath = dest.toPath();
        // 尝试移动文件
        // 使用REPLACE_EXISTING选项来替换目标位置已存在的文件
        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // 注意：如果目标文件和源文件在同一个文件系统中，并且文件系统支持原子移动，
        // 那么上面的调用会尝试原子地移动文件。
        // 如果目标文件和源文件在不同的文件系统上，或者文件系统不支持原子移动，
        // 那么会执行一个复制后删除原文件的操作。
    }


    /**
     * 删除目标文件
     *
     * @param file 目标文件
     * @return     文件删除结果
     */
    @Alpha
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        } else {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                File[] var2 = files;
                int var3 = files.length;
                for(int var4 = 0; var4 < var3; ++var4) {
                    File f = var2[var4];
                    deleteFile(f);
                }
            }
            return file.delete();
        }
    }


    /**
     * 获取文件扩展名（NIO 方法）
     * @param filePath 文件路径
     * @return 扩展名（如 "jpg"），若无扩展名返回空字符串
     */
    @Alpha
    public static String getFileExtension(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     *   重载方法，支持传入多个目录
     * @param dir  入口文件夹
     * @return     文件集合
     */
    @Alpha
    public static List<File> getFileList(File dir) {
        List<File> list = new ArrayList<File>();
        if (dir.isDirectory() && null != dir.listFiles() && dir.listFiles().length > 0) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()){
                    List<File> fileList = getFileList(file);
                    list.addAll(fileList);
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }


    /**
     *   获取过滤后的文件列表
     *
     * @param dir          入口文件
     * @param fileFilter   过滤器实现类
     * @return             过滤后的文件集合
     */
    @Alpha
    public static List<File> getFilteredFileList(File dir, FileFilter fileFilter){
        List<File> fileList = new ArrayList<File>();
        for(File file : dir.listFiles()){
            if (file.isFile()){
                if(fileFilter.accept(file)){
                    fileList.add(file);
                }
            } else {
                fileList.addAll(getFilteredFileList(file, fileFilter));
            }
        }
        return fileList;
    }



    /**
     *     计算文件的MD5
     *
     * @param file 需要计算的文件
     * @return     计算的文件MD5值
     */
    @Alpha
    public static String getFileMD5(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            InputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /***
     * 计算文件的SHA1
     *
     * @param file 需要计算SHA1的文件
     * @return     文件的SHA1值
     */
    @Alpha
    public static String getFileSHA1(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            InputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }






    /**
     * 根据文件路径读取byte[] 数组
     *
     * @param filePath       文件路径
     * @return               文本内容，byte[]类型
     * @throws IOException   IO异常
     */
    public static byte[] readFileAsBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }



    /**
     * Reads the contents of a file line by line to a List of Strings. The file is always closed.
     *
     *  Use Default CharSet UTF-8
     *
     * @param file     the file to read, must not be {@code null}
     * @return the list of Strings representing each line in the file, never {@code null}
     * @throws NullPointerException if file is {@code null}.
     * @throws IOException if an I/O error occurs, including when the file does not exist, is a directory rather than a
     *         regular file, or for some other reason why the file cannot be opened for reading.
     * @throws java.nio.charset.UnsupportedCharsetException if the named charset is unavailable.
     * @since 0.0.1.10
     */
    @Alpha
    public static List<String> readLines(final File file) throws IOException {
        return readLines(file, CHARSET_UTF_8);
    }


    /**
     * Reads the contents of a file line by line to a List of Strings. The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return the list of Strings representing each line in the file, never {@code null}
     * @throws NullPointerException if file is {@code null}.
     * @throws IOException if an I/O error occurs, including when the file does not exist, is a directory rather than a
     *         regular file, or for some other reason why the file cannot be opened for reading.
     * @throws java.nio.charset.UnsupportedCharsetException if the named charset is unavailable.
     * @since 0.0.1.10
     */
    @Alpha
    public static List<String> readLines(final File file, final String charsetName) throws IOException {
        return readLines(file, Charset.forName(charsetName));
    }

    /**
     *   按行读取文件，将其保存到字符串集合中返回
     *
     * @param file           需要读的文件
     * @param charset        文件字符集
     * @return               字符串集合 一行一项
     * @throws IOException   IOException
     * @since 0.0.1.10
     */
    @Alpha
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        return Files.readAllLines(file.toPath(), charset);
    }


    /**
     *  读取文件内容，作为字符串返回
     *
     * @param filePath         文件路径
     * @return                 文件内容
     * @throws IOException     文件IO异常
     * @since 0.0.1.10
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        }
        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流
        FileInputStream fis = new FileInputStream(filePath);
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];
        // 用于保存实际读取的字节数
        int hasRead = 0;
        while ( (hasRead = fis.read(bbuf)) > 0 ) {
            sb.append(new String(bbuf, 0, hasRead));
        }
        fis.close();
        return sb.toString();
    }


    @Alpha
    public static String readFileToString(final File file) throws IOException {
        return readFileToString(file, StandardCharsets.UTF_8);
    }

    /**
     * Reads the contents of a file into a String. The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return the file contents, never {@code null}
     * @throws NullPointerException if file is {@code null}.
     * @throws IOException if an I/O error occurs, including when the file does not exist, is a directory rather than a
     *         regular file, or for some other reason why the file cannot be opened for reading.
     * @throws java.nio.charset.UnsupportedCharsetException if the named charset is unavailable.
     * @since 0.0.1.10
     */
    @Alpha
    public static String readFileToString(final File file, final String charsetName) throws IOException {
        return readFileToString(file, Charset.forName(charsetName));
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @param charset the requested charset, {@code null} means platform default
     * @return the file contents, never {@code null}
     * @throws NullPointerException if file is {@code null}.
     * @throws IOException if an I/O error occurs, including when the file does not exist, is a directory rather than a
     *         regular file, or for some other reason why the file cannot be opened for reading.
     * @since 0.0.1.10
     */
    @Alpha
    public static String readFileToString(final File file, final Charset charset) throws IOException {
        InputStream input =  file.toPath().getFileSystem().provider().newInputStream(file.toPath());
        if (null == input) {
            throw new NullPointerException("input");
        }
        String content;
        try{
            content = toString(input, charset);
        } catch (Throwable var7) {
            if (null != input) {
                try {
                    input.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }
            throw var7;
        }
        if (null != input) {
            input.close();
        }
        return content;
    }
    /**
     *
     * @param input    输入流
     * @param charset  字符集
     * @return         返回的字符串
     * @throws IOException
     */
    private static String toString(InputStream input, Charset charset) throws IOException {
        StringWriter sw = new StringWriter();
        String var3;
        try {
            IOUtil.copy((InputStream)input, (Writer)sw, (Charset)charset);
            var3 = sw.toString();
        } catch (Throwable var6) {
            try {
                sw.close();
            } catch (Throwable var5) {
                var6.addSuppressed(var5);
            }
            throw var6;
        }
        sw.close();
        return var3;
    }


    /**
     * Writes a CharSequence to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @throws IOException in case of an I/O error
     * @since 0.0.2-SNAPSHOT
     */
    @Alpha
    public static void write(final File file, final CharSequence data) throws IOException {
        write(file, data, StandardCharsets.UTF_8);
    }

    /**
     * Writes a CharSequence to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @param charset the name of the requested charset, {@code null} means platform default
     * @throws IOException in case of an I/O error
     * @since 0.0.1.10
     */
    @Alpha
    public static void write(final File file, final CharSequence data, final Charset charset) throws IOException {
        String dataStr = Objects.toString(data, null);
        try (OutputStream out = newOutputStream(Objects.requireNonNull(file, "file").toPath(), false)) {
            if (dataStr != null) {
                // Use Charset#encode(String), since calling String#getBytes(Charset) might result in
                // NegativeArraySizeException or OutOfMemoryError.
                // The underlying OutputStream should not be closed, so the channel is not closed.
                Channels.newChannel(out).write(charset.encode(dataStr));
            }
        }
    }

    /**
     * Empty {@link LinkOption} array.
     */
    private static final LinkOption[] EMPTY_LINK_OPTION_ARRAY = {};
    private static final OpenOption[] OPEN_OPTIONS_TRUNCATE = { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING };
    private static final OpenOption[] OPEN_OPTIONS_APPEND = { StandardOpenOption.CREATE, StandardOpenOption.APPEND };
    /**
     * A LinkOption used to follow link in this class, the inverse of {@link LinkOption#NOFOLLOW_LINKS}.
     *
     * @since 0.0.1.10
     */
    private static final LinkOption NULL_LINK_OPTION = null;
    /**
     * Empty {@link OpenOption} array.
     */
    private static final OpenOption[] EMPTY_OPEN_OPTION_ARRAY = {};
    /**
     *    按行写入字符串集合
     *
     * @param file       文件
     * @param lines      lines
     * @param charset    字符集
     * @throws IOException IOException
     * @since 0.0.1.10
     */
    @Alpha
    public static void writeLines(final File file, final Collection<?> lines, final Charset charset) throws IOException {
        try (OutputStream out = new BufferedOutputStream(newOutputStream(Objects.requireNonNull(file, "file").toPath(), false))) {
            writeLines(lines, null, out, charset);
        }
    }
    /**
     *     输出流
     *
     * @param path    路径
     * @param append  append
     * @return        输出流
     * @throws IOException
     */
    private static OutputStream newOutputStream(final Path path, final boolean append) throws IOException {
        return newOutputStream(path, EMPTY_LINK_OPTION_ARRAY, append ? OPEN_OPTIONS_APPEND : OPEN_OPTIONS_TRUNCATE);
    }

    /**
     *
     * @param path  路径
     * @param linkOptions   linkOptions
     * @param openOptions   openOptions
     * @return              输出流
     * @throws IOException
     */
    private static OutputStream newOutputStream(final Path path, final LinkOption[] linkOptions, final OpenOption... openOptions) throws IOException {
        if (!exists(path, linkOptions)) {
            createParentDirectories(path, linkOptions != null && linkOptions.length > 0 ? linkOptions[0] : NULL_LINK_OPTION);
        }
        final List<OpenOption> list = new ArrayList<>(Arrays.asList(openOptions != null ? openOptions : EMPTY_OPEN_OPTION_ARRAY));
        list.addAll(Arrays.asList(linkOptions != null ? linkOptions : EMPTY_LINK_OPTION_ARRAY));
        return Files.newOutputStream(path, list.toArray(EMPTY_OPEN_OPTION_ARRAY));
    }

    /**
     *    是否存在
     * @param path 路径
     * @param options   可选参数
     * @return          判断结果
     */
    private static boolean exists(final Path path, final LinkOption... options) {
        Objects.requireNonNull(path, "path");
        return options != null ? Files.exists(path, options) : Files.exists(path);
    }

    /**
     *
     * @param path       路径
     * @param linkOption linkOption
     * @param attrs      动态attrs
     * @return           路径
     * @throws IOException
     */
    private static Path createParentDirectories(final Path path, final LinkOption linkOption, final FileAttribute<?>... attrs) throws IOException {
        Path parent = getParent(path);
        parent = linkOption == LinkOption.NOFOLLOW_LINKS ? parent : readIfSymbolicLink(parent);
        if (parent == null) {
            return null;
        }
        final boolean exists = linkOption == null ? Files.exists(parent) : Files.exists(parent, linkOption);
        return exists ? parent : Files.createDirectories(parent, attrs);
    }

    /**
     *       获取父路径
     *
     * @param path 路径
     * @return     父路径
     */
    private static Path getParent(final Path path) {
        return path == null ? null : path.getParent();
    }

    /**
     *   获取readIfSymbolicLink
     *
     * @param path  路径
     * @return      结果路径
     * @throws IOException
     */
    private static Path readIfSymbolicLink(final Path path) throws IOException {
        return path != null ? Files.isSymbolicLink(path) ? Files.readSymbolicLink(path) : path : null;
    }

    /**
     *    按行写入文件
     *
     * @param lines   lines
     * @param lineEnding  lineEnding
     * @param output   输出流
     * @param charset  字符集
     * @throws IOException
     */
    private static void writeLines(final Collection<?> lines, String lineEnding, final OutputStream output,
                                  Charset charset) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = System.lineSeparator();
        }
        if (StandardCharsets.UTF_16.equals(charset)) {
            // don't write a BOM
            charset = StandardCharsets.UTF_16BE;
        }
        final byte[] eolBytes = lineEnding.getBytes(charset);
        for (final Object line : lines) {
            if (line != null) {
                if (line.toString() != null) {
                    // Use Charset#encode(String), since calling String#getBytes(Charset) might result in
                    // NegativeArraySizeException or OutOfMemoryError.
                    // The underlying OutputStream should not be closed, so the channel is not closed.
                    Channels.newChannel(output).write(charset.encode(line.toString()));
                }
            }
            output.write(eolBytes);
        }
    }



    /**
     * 获取按最后修改时间排序的文件列表
     *
     * @param directory 文件夹路径
     * @return 按最后修改时间排序的文件数组  最早修改的在前，最近修改/写入的排在最后
     */
    public static File[] getSortedFilesByLastModified(String directory) {
        List<File> list = getFileListSortedByLastModified(directory);
        return list.toArray(new File[list.size()]);
    }

    /**
     * 获取按最后修改时间排序的文件列表
     *
     * @param directory 文件夹路径
     * @return 按最后修改时间(正序)排序的文件列表
     */
    public static List<File> getFileListSortedByLastModified(String directory) {
        File folder = new File(directory);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("指定的路径不是一个有效的文件夹");
        }
        // 递归获取所有文件
        List<File> allFiles = FileUtil.getFileList(folder);
        // 按最后修改时间排序（最先修改的排在前面）
        Collections.sort(allFiles, Comparator.comparingLong(File::lastModified));
        return allFiles;
    }


    public static void deleteEmptyDir(File f) {
        if (f.isDirectory()) {
            if (f.listFiles().length == 0){
                FileUtil.deleteFile(f);
                logger.fine("删除空文件夹["+f.getAbsolutePath()+"]");
            } else {
                for (File sub : f.listFiles()) {
                    if (sub.isDirectory()) {
                        deleteEmptyDir(sub);
                    }
                }
            }
        }
    }


    /**
     *    清空目录下的文件(不包括目录本省)
     *
     * @param dir  目录
     * @return     删除结果
     */
    public static boolean cleanDirectory(File dir) {
        boolean flag = false;
        if (!dir.exists()) {
            return flag;
        } else if (!dir.isDirectory()) {
            return flag;
        } else {
            String[] tempList = dir.list();
            File temp = null;
            for(int i = 0; i < tempList.length; ++i) {
                String path = dir.getPath();
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    cleanDirectory(temp);
                    temp.delete();
                    flag = true;
                }
            }
            return flag;
        }
    }

}
