package com.acanx.util;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64UtilTest {

    public static final Base64.Decoder decoder = Base64.getDecoder();
    public static final Base64.Encoder encoder = Base64.getEncoder();


    final String text = "字串文字华人1111111111111111111111111";

    @Test
    public void encodeTest() throws UnsupportedEncodingException {
        final byte[] textByte = text.getBytes("UTF-8");
        //编码
        final String encodedText = encoder.encodeToString(textByte);
        System.out.println(encodedText);
        //解码
        System.out.println(new String(decoder.decode(encodedText), "UTF-8"));

    }

    /**
     * base64 demo 测试
     */
    @Test
    public void encodeBase64Test() throws UnsupportedEncodingException {
        System.out.println(text);
        String encode = Base64Util.encodeBase64(text);
        System.out.println(encode);
    }

    @Test
    public void decodeBase64Test() throws UnsupportedEncodingException {
        System.out.println(text);
        String encode = Base64Util.encodeBase64(text);
        System.out.println(Base64Util.decodeBase64(encode));
    }

    @Test
    public void testEncodeBase64Test() {

    }

    @Test
    public void testDecodeBase64Test() {

    }
}