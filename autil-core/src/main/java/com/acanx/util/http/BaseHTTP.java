package com.acanx.util.http;

import com.acanx.annotation.Alpha;
import com.acanx.constant.HTTPC;
import com.acanx.constant.MIMEC;
import com.acanx.util.URLUtil;
import com.acanx.util.StringUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 基础HTTP 工具类 (JDK 8)
 * 功能：
 * 1. 支持 GET/POST/PUT/DELETE/PATCH 方法
 * 2. 自定义请求头（包括 Authorization）
 * 3. 支持 Cookie 设置
 * 4. 自动拼接 URL 参数
 * 5. 支持多种请求体格式（JSON/FormData/XML等）
 * 6. 完整响应信息记录（状态码、头信息、内容、错误等）
 */
public class BaseHTTP {

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
    @Alpha
    public static void execute(HContext context) {
        // HttpURLConnection connection = null;
        HRequest config = context.getRequest();
        HResponse response = new HResponse();
        try {
            // 1. 构建完整URL
            String fullUrl = buildFullUrl(config.getUrl(), config.getParams());
            // 2. 创建 HttpClient
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(
                            config.getConnectTimeout() > 0 ?
                                    config.getConnectTimeout() : DEFAULT_CONNECT_TIMEOUT))
                    .version(config.getHttpVersion()) // 默认使用HTTP/2（根据业务需求调整）
                    .build();
            // 3. 构建 HttpRequest
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .timeout(Duration.ofMillis(
                            config.getReadTimeout() > 0 ?
                                    config.getReadTimeout() : DEFAULT_READ_TIMEOUT));
            // 4. 设置请求方法及请求体
            String method = config.getMethod().toUpperCase();
            boolean isBodyAllowed = (!HTTPC.GET.equalsIgnoreCase(method) && !HTTPC.DELETE.equalsIgnoreCase(method));
            if (isBodyAllowed && StringUtil.isNotBlank(config.getBody())) {
                requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(config.getBody()));
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }
            // 5. 设置请求头
            config.getHeaders().forEach(requestBuilder::header);
            // 6. 设置 Cookie
            if (config.getCookies() != null && !config.getCookies().isEmpty()) {
                String cookies = config.getCookies().entrySet().stream()
                        .map(e -> URLUtil.encodeParameter(e))
                        .collect(Collectors.joining("; "));
                requestBuilder.header("Cookie", cookies);
            }
            // 7. 发送请求并获取响应
            HttpResponse<String> httpResponse = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            // 8. 处理响应数据
            response.setStatusCode(httpResponse.statusCode());
            response.setHeaders(httpResponse.headers().map());
            response.setBody(httpResponse.body());
        } catch (IOException e) {
            response.setError(true);
            response.setErrorMessage(e.getMessage());
            response.setStackTrace(getStackTrace(e));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // finally {
            // if (connection != null) {
            //    connection.disconnect();
            // }
        // }
        context.setResponse(response);
    }

    /**
     * 构建完整URL（带参数）
     *
     * @param baseUrl   Base资源链接
     * @param params    URL参数
     * @return          完整的URL
     */
    @Alpha
    public static String buildFullUrl(String baseUrl, Map<String, String> params) {
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
    @Deprecated
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
    @Alpha
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
    @Alpha
    public static HResponse get(String url) {
        return getHttpResponse(HRequest.builder().url(url).build());
    }

    /**
     * GET请求
     *
     * @param url        资源地址
     * @param params     URL参数
     * @return           响应
     */
    @Alpha
    public static HResponse get(String url, Map<String, String> params) {
        return getHttpResponse(HRequest.builder().url(url).params(params).build());
    }

    /**
     *  POST请求
     *
     * @param url     资源地址
     * @param body    请求体
     * @return        响应
     */
    @Alpha
    public static HResponse post(String url, String body) {
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
    @Alpha
    public static HResponse post(String url, Map<String, String> headers, String body) {
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
    @Alpha
    public static HResponse post(String url, Map<String, String> params, Map<String, String> headers, String body) {
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
    @Alpha
    public static HResponse post(String url, Map<String, String> params, Map<String, String> cookie, Map<String, String> headers, String body) {
        return getHttpResponse(HRequest.builder().method(HTTPC.POST).url(url).params(params).contentType(MIMEC.JSON).cookies(cookie).headers(headers).body(body).build());
    }


    /**
     *  POST请求
     *
     * @param url         资源地址
     * @param formData    表单数据
     * @return            响应
     */
    @Alpha
    public static HResponse postForm(String url, Map<String, String> formData) {
        String formBody = formData.entrySet().stream()
                .map(e -> URLUtil.encodeParameter(e.getKey(), e.getValue()))
                .collect(Collectors.joining("&"));
        return getHttpResponse(HRequest.builder().method(HTTPC.POST).url(url).contentType(MIMEC.X_WWW_FORM_URLENCODED).body(formBody).build());
    }

    /**
     *   获取HTTP响应
     *
     * @param request   请求对象
     * @return          响应
     */
    @Alpha
    public static HResponse getHttpResponse(HRequest request) {
        HContext context = new HContext(request);
        execute(context);
        return context.getResponse();
    }

}