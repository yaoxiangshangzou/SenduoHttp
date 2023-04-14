package com.example.huajietest.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :      SunHuaJie
 * @date :        2023/4/12-00:32
 * @email :       1214495199@qq.com
 * @description : 存储网络请求返回的数据
 */
public class Response {
    int code;
    int contentLength=-1;
    Map<String,String> headers=new HashMap<>();
    String body;
    boolean isKeepAlive;

    public Response(){

    }

    public Response(int code, int contentLength, Map<String, String> headers, String body, boolean isKeepAlive) {
        this.code = code;
        this.contentLength = contentLength;
        this.headers = headers;
        this.body = body;
        this.isKeepAlive = isKeepAlive;
    }
    public int getCode() {
        return code;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }
}
