package com.acanx.util.http;

import com.acanx.constant.HTTPC;

import java.net.http.HttpClient;
import java.util.Map;

/**
 *  HttpRequest
 */
public class HRequest {

    // 其他字段保持不变
    private HttpClient.Version httpVersion = HttpClient.Version.HTTP_2;
    private String method = HTTPC.GET;
    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private String body;
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
            config.headers.put(HTTPC.AUTHORIZATION, token);
            return this;
        }

        public Builder contentType(String type) {
            if (config.headers == null) {
                config.headers = new java.util.HashMap<>();
            }
            config.headers.put(HTTPC.CONTENT_TYPE, type);
            return this;
        }

        public HRequest build() {
            return config;
        }
    }
}
