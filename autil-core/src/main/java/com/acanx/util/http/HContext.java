package com.acanx.util.http;

public class HContext {

    private HRequest request;

    private HResponse response;

    public HContext() {
    }

    public HContext(HRequest request) {
        this.request = request;
    }

    public HRequest getRequest() {
        return request;
    }

    public void setRequest(HRequest request) {
        this.request = request;
    }

    public HResponse getResponse() {
        return response;
    }

    public void setResponse(HResponse response) {
        this.response = response;
    }
}
