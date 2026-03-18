package com.acanx.util;

import com.acanx.c.Const;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 *   IO输入输出工具类
 *
 * @since 0.0.1.10
 */
public class IOUtil {
    /**
     * 构造函数
     * @hidden
     */
    private IOUtil() {
    }

    /**
     *   拷贝
     *
     * @param input   Reader
     * @param output  Writer
     * @return        count
     * @throws IOException IOException
     */
    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int)count;
    }

    /**
     *   拷贝
     *
     * @param input    InputStream
     * @param writer   Writer
     * @param inputCharset 字符集
     * @throws IOException  IOException
     */
    public static void copy(InputStream input, Writer writer, Charset inputCharset) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, inputCharset);
        copy((Reader)reader, (Writer)writer);
    }

    /**
     *  大拷贝
     *
     * @param input   Reader
     * @param output  Writer
     * @return        长度
     * @throws IOException   IOException
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[Const.INT_4096]);
    }

    /**
     *   大拷贝
     *
     * @param input   Reader
     * @param output  Writer
     * @param buffer  字符数组buffer
     * @return        长度
     * @throws IOException IOException
     */
    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count;
        int n;
        for(count = 0L; -1 != (n = input.read(buffer)); count += (long)n) {
            output.write(buffer, Const.INT_0, n);
        }
        return count;
    }

}
