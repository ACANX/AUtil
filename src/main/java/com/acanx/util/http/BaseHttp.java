package com.acanx.util.http;

import com.acanx.constant.HttpC;
import com.acanx.constant.MIMEC;
import com.acanx.util.URLUtil;
import com.acanx.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 增强版 HTTP 工具类 (JDK 8)
 * 功能：
 * 1. 支持 GET/POST/PUT/DELETE/PATCH 方法
 * 2. 自定义请求头（包括 Authorization）
 * 3. 支持 Cookie 设置
 * 4. 自动拼接 URL 参数
 * 5. 支持多种请求体格式（JSON/FormData/XML等）
 * 6. 完整响应信息记录（状态码、头信息、内容、错误等）
 */
public class BaseHttp {

    /**
     * 默认连接配置 10秒
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    /**
     *  15秒
     */
    private static final int DEFAULT_READ_TIMEOUT = 15000;

    /**
     * 发送 HTTP 请求
     *
     * @param context 请求上下文
     */
    public static void execute(HttpContext context) {
        HttpURLConnection connection = null;
        HttpRequest config = context.getRequest();
        HttpResponse response = new HttpResponse();
        try {
            // 1. 构建完整URL（带参数）
            String fullUrl = buildFullUrl(config.getUrl(), config.getParams());
            URL url = new URL(fullUrl);
            // 2. 创建连接
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(config.getMethod().toUpperCase());
            connection.setConnectTimeout(config.getConnectTimeout() > 0 ?
                    config.getConnectTimeout() : DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(config.getReadTimeout() > 0 ?
                    config.getReadTimeout() : DEFAULT_READ_TIMEOUT);
            // 3. 设置请求头
            setHeaders(connection, config.getHeaders());
            // 4. 设置 Cookie
            if (config.getCookies() != null && !config.getCookies().isEmpty()) {
                String cookieHeader = config.getCookies().entrySet().stream()
                        .map(e -> URLUtil.encodeParameter(e))
                        .collect(Collectors.joining("; "));
                connection.setRequestProperty(HttpC.COOKIE, cookieHeader);
            }
            // 5. 处理请求体（POST/PUT/PATCH）
            if (StringUtil.isNotBlank(config.getBody()) &&
                    !HttpC.GET.equalsIgnoreCase(config.getMethod()) && !HttpC.DELETE.equalsIgnoreCase(config.getMethod())) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = config.getBody().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            // 6. 获取响应状态码
            response.setStatusCode(connection.getResponseCode());
            // 7. 获取响应头
            Map<String, List<String>> responseHeaders = connection.getHeaderFields();
            response.setHeaders(responseHeaders);
            // 8. 获取响应内容
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            response.getStatusCode() < 400 ? connection.getInputStream() : connection.getErrorStream(),
                            StandardCharsets.UTF_8))) {
                String content = br.lines().collect(Collectors.joining("\n"));
                response.setBody(content);
            }
        } catch (IOException e) {
            response.setError(true);
            response.setErrorMessage(e.getMessage());
            response.setStackTrace(getStackTrace(e));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        context.setResponse(response);
    }

    /**
     * 构建完整URL（带参数）
     *
     * @param baseUrl   Base资源链接
     * @param params    URL参数
     * @return
     */
    private static String buildFullUrl(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringJoiner sj = new StringJoiner("&", baseUrl.contains("?") ? "&" : "?", "");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sj.add(URLUtil.encodeParameter(entry));
        }
        return baseUrl + sj.toString();
    }

    /**
     * 设置请求头
     *
     * @param connection      连接
     * @param headers         请求头
     */
    private static void setHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获取异常堆栈
     *
     * @param e     异常
     * @return      堆栈跟踪
     */
    private static String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }




    /**  ========== 快捷方法 ========== */

    /**
     *   GET请求
     *
     * @param url 资源地址
     * @return    响应
     */
    public static HttpResponse get(String url) {
        return getHttpResponse(HttpRequest.builder().url(url).build());
    }

    /**
     * GET请求
     *
     * @param url        资源地址
     * @param params     URL参数
     * @return           响应
     */
    public static HttpResponse get(String url, Map<String, String> params) {
        return getHttpResponse(HttpRequest.builder().url(url).params(params).build());
    }

    /**
     *  POST请求
     *
     * @param url     资源地址
     * @param body    请求体
     * @return        响应
     */
    public static HttpResponse post(String url, String body) {
        return post(url, null,null, null, body);
    }

    /**
     * POST请求
     *
     * @param url     资源地址
     * @param headers 请求头
     * @param body    请求体
     * @return        响应
     */
    public static HttpResponse post(String url, Map<String, String> headers, String body) {
        return post(url, null,null, headers, body);
    }

    /**
     * POST请求
     *
     * @param url       资源地址
     * @param params    URL参数
     * @param headers   请求头
     * @param body      请求体
     * @return          响应
     */
    public static HttpResponse post(String url, Map<String, String> params, Map<String, String> headers, String body) {
        return post(url, params, null, headers, body);
    }

    /**
     *  POST请求
     *
     * @param url     资源地址
     * @param params  URL参数
     * @param cookie  Cookie
     * @param headers 请求头
     * @param body    请求体
     * @return        响应
     */
    public static HttpResponse post(String url, Map<String, String> params, Map<String, String> cookie, Map<String, String> headers, String body) {
        return getHttpResponse(HttpRequest.builder().method(HttpC.POST).url(url).params(params).contentType(MIMEC.JSON).cookies(cookie).headers(headers).body(body).build());
    }


    /**
     *  POST请求
     *
     * @param url         资源地址
     * @param formData    表单数据
     * @return            响应
     */
    public static HttpResponse postForm(String url, Map<String, String> formData) {
        String formBody = formData.entrySet().stream()
                .map(e -> URLUtil.encodeParameter(e.getKey(), e.getValue()))
                .collect(Collectors.joining("&"));
        return getHttpResponse(HttpRequest.builder().method(HttpC.POST).url(url).contentType(MIMEC.X_WWW_FORM_URLENCODED).body(formBody).build());
    }

    /**
     *   获取HTTP响应
     *
     * @param request   请求对象
     * @return          响应
     */
    private static HttpResponse getHttpResponse(HttpRequest request) {
        HttpContext context = new HttpContext(request);
        execute(context);
        return context.getResponse();
    }

}