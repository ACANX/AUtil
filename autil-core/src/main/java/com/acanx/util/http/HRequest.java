package com.acanx.util.http;

import com.acanx.c.HTTPConst;

import java.net.http.HttpClient;
import java.util.Map;

/**
 *  HttpRequest
 */
public class HRequest {

    // 其他字段保持不变
    private HttpClient.Version httpVersion = HttpClient.Version.HTTP_2;
    private String method = HTTPConst.GET;
    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private String body;
    /**
     *  原始字节请求体（二进制保真；与 {@link #body} 互斥，非空时优先）
     *
     *  <p>背景：{@code body(String)} 经 {@code BodyPublishers.ofString}（UTF-8）发送，
     *  非 ASCII 字节不可逆，无法承载任意二进制内容（如文件直传）。本字段由
     *  {@link Builder#bodyBytes(byte[])} 设置，发送时走 {@code BodyPublishers.ofByteArray}。</p>
     */
    private byte[] bodyBytes;
    private int connectTimeout;
    private int readTimeout;
    // Builder 模式
    public static Builder builder() {
        return new Builder();
    }

    // Getters

    public HttpClient.Version getHttpVersion() {return httpVersion;}
    public String getMethod() { return method; }
    public String getUrl() { return url; }
    public Map<String, String> getParams() { return params; }
    public Map<String, String> getHeaders() { return headers; }
    public Map<String, String> getCookies() { return cookies; }
    public String getBody() { return body; }
    public byte[] getBodyBytes() { return bodyBytes; }
    public int getConnectTimeout() { return connectTimeout; }
    public int getReadTimeout() { return readTimeout; }

    /**
     * Builder 类
     */
    public static class Builder {
        private final HRequest config = new HRequest();

        public Builder httpVersion(HttpClient.Version version) {
            config.httpVersion = version;
            return this;
        }

        public Builder method(String method) {
            config.method = method.toUpperCase();
            return this;
        }

        public Builder url(String url) {
            config.url = url;
            return this;
        }

        public Builder params(Map<String, String> params) {
            config.params = params;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            config.headers = headers;
            return this;
        }

        public Builder cookies(Map<String, String> cookies) {
            config.cookies = cookies;
            return this;
        }

        public Builder body(String body) {
            config.body = body;
            return this;
        }

        /**
         * 设置原始字节请求体（二进制保真；发送时走 {@code BodyPublishers.ofByteArray}）。
         *
         * <p>与 {@link #body(String)} 互斥：设置后优先使用字节体，String 体被忽略。</p>
         *
         * @param bodyBytes 原始字节（可为 null，表示不设置）
         * @return 本 Builder
         */
        public Builder bodyBytes(byte[] bodyBytes) {
            config.bodyBytes = bodyBytes;
            return this;
        }

        public Builder connectTimeout(int timeout) {
            config.connectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(int timeout) {
            config.readTimeout = timeout;
            return this;
        }

        public Builder authorization(String token) {
            if (config.headers == null) {
                config.headers = new java.util.HashMap<>();
            }
            config.headers.put(HTTPConst.AUTHORIZATION, token);
            return this;
        }

        public Builder contentType(String type) {
            if (config.headers == null) {
                config.headers = new java.util.HashMap<>();
            }
            config.headers.put(HTTPConst.CONTENT_TYPE, type);
            return this;
        }

        public HRequest build() {
            return config;
        }
    }
}
