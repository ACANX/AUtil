package com.acanx.utils.incubator;

import com.acanx.constant.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 *
 * @since 0.0.1.10
 */
public class IOUtil {

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int)count;
    }

    public static void copy(InputStream input, Writer writer, Charset inputCharset) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, inputCharset);
        copy((Reader)reader, (Writer)writer);
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[Constant.INT_4096]);
    }

    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count;
        int n;
        for(count = 0L; -1 != (n = input.read(buffer)); count += (long)n) {
            output.write(buffer, Constant.INT_0, n);
        }
        return count;
    }

}
