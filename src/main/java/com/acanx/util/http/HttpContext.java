package com.acanx.util.http;

public class HttpContext {

    private HttpRequest request;

    private HttpResponse response;

    public HttpContext() {
    }

    public HttpContext(HttpRequest request) {
        this.request = request;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }
}
