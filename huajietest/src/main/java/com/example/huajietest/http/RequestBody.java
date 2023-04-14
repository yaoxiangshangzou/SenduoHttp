package com.example.huajietest.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/5/5
 * * 文件描述：在post请求中，需要有requestBody格式的参数，此类存储post请求中参数的数据
 * * 修改历史：2018/5/5 11:27*************************************
 **/

public class RequestBody {

    /**
     * 表单提交 使用urlencoded编码,这里也可以使用json方式
     */
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    private static final String CHARSET = "UTF-8";

    Map<String,String> encodeBodys = new HashMap<>();

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public int getContentLength(){
        return getBody().getBytes().length;
    }

    public String getBody(){

        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String,String> entry : encodeBodys.entrySet()){
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        if(sb.length() != 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    //通过JDK的url编码
    public RequestBody add(String key,String value){
        try {
            encodeBodys.put(URLEncoder.encode(key,CHARSET),URLEncoder.encode(value,CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }
}
