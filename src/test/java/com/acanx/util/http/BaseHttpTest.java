package com.acanx.util.http;

import com.acanx.constant.HTTPC;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class BaseHttpTest {

    @Test
    void get() {
        // 构建请求
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "10");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer abc123");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        HContext context = new HContext(HRequest.builder().url("https://httpbin.org/get").params(params).headers(headers).build());
        BaseHTTP.execute(context);
        HResponse response = context.getResponse();
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Response: \n" + response.getBody());
    }

    @Test
    void testGet() {
    }

    @Test
    void post() {
    }

    @Test
    void postJson() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer abc123");
        headers.put(HTTPC.CONTENT_TYPE, "application/json");
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "xyz789");
        // 这里需要重新实现
        headers.put(HTTPC.COOKIE, cookies.toString());
        String jsonBody = "{\"username\":\"admin\",\"password\":\"123456\"}";
        HResponse response1 = BaseHTTP.post("https://httpbin.org/post", headers, jsonBody);
        System.out.println(response1.getBody());
    }

    @Test
    void postJson2() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "xyz789");
        String jsonBody = "{\"username\":\"admin\",\"password\":\"123456\"}";
        HContext context = new HContext(HRequest.builder()
                .method("POST")
                .url("https://httpbin.org/post")
                .cookies(cookies)
                .contentType("application/json")
                .body(jsonBody)
                .build());
        // 或者使用完整构建器
        BaseHTTP.execute(context);
        HResponse response2 =context.getResponse();
        System.out.println(response2.getBody());
    }

    @Test
    void postForm() {
        Map<String, String> formData = new HashMap<>();
        formData.put("username", "admin");
        formData.put("password", "123456");
        HResponse response = BaseHTTP.postForm("https://httpbin.org/post", formData);
        System.out.println(response.getBody());
    }
}