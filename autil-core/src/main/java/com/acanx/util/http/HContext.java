package com.acanx.util.http;

import java.util.HashMap;
import java.util.Map;

/**
 *   HContext
 */
public class HContext {

    private HRequest request;

    private HResponse response;

    private Map<String, Object> attachment = new HashMap<String, Object>();

    /**
     *  构造函数
     */
    public HContext() {
    }

    /**
     *  构造函数
     *
     * @param request
     */
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

    public Object get(String key) {
        return attachment.get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        return (T) attachment.get(key);
    }

    public void put(String key, Object value) {
        this.attachment.put(key, value);
    }

    public boolean containsKey(String key) {
        return this.attachment.containsKey(key);
    }

}
