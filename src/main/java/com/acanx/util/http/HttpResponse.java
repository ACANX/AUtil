package com.acanx.util.http;

import com.acanx.constant.HttpC;

import java.util.List;
import java.util.Map;

/**
 *  HttpResponse
 */
public class HttpResponse {

    private int statusCode;
    private Map<String, List<String>> headers;
    private String body;
    private boolean error;
    private String errorMessage;
    private String stackTrace;

    /**
     * Getters and Setters
     * @return StatusCode
     */
    public int getStatusCode() { return statusCode; }

    /**
     *  statusCode
     * @param statusCode StatusCode
     */
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    /**
     *  Headers
     * @return  Headers
     */
    public Map<String, List<String>> getHeaders() { return headers; }

    /**
     * Headers
     * @param headers Headers
     */
    public void setHeaders(Map<String, List<String>> headers) { this.headers = headers; }

    /**
     *   Body
     * @return Body
     */
    public String getBody() { return body; }

    /**
     *  Body
     * @param body Body
     */
    public void setBody(String body) { this.body = body; }

    /**
     * Error
     * @return Error
     */
    public boolean isError() { return error; }

    /**
     *   Error
     * @param error Error
     */
    public void setError(boolean error) { this.error = error; }

    /**
     * ErrorMessage
     * @return ErrorMessage
     */
    public String getErrorMessage() { return errorMessage; }

    /**
     *   ErrorMessage
     * @param errorMessage ErrorMessage
     */
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    /**
     * StackTrace
     * @return StackTrace
     */
    public String getStackTrace() { return stackTrace; }

    /**
     * StackTrace
     * @param stackTrace StackTrace
     */
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    /**
     * 获取特定响应头的第一个值
     * @param name  key值
     * @return   获取特定响应头的第一个值
     */
    public String getHeader(String name) {
        if (headers == null) return null;
        List<String> values = headers.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    /**
     *  获取所有Cookie
     * @return   所有Cookie
     */
    public Map<String, String> getCookies() {
        if (headers == null){
            return null;
        }
        List<String> cookieHeaders = headers.get(HttpC.SET_COOKIE);
        if (cookieHeaders == null){
            return null;
        }
        Map<String, String> cookies = new java.util.HashMap<>();
        for (String header : cookieHeaders) {
            String[] parts = header.split(";")[0].split("=");
            if (parts.length >= 2) {
                cookies.put(parts[0].trim(), parts[1].trim());
            }
        }
        return cookies;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", content='" + (body != null && body.length() > 100 ?
                body.substring(0, 100) + "..." : body) + '\'' +
                ", error=" + error +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }


}
