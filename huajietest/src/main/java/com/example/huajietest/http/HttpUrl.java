package com.example.huajietest.http;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @author :      SunHuaJie
 * @date :        2023/4/11-21:16
 * @email :       1214495199@qq.com
 * @description :
 */
public class HttpUrl {
    String protocol;
    String host;
    String file;
    int port;

    public HttpUrl(String url) throws MalformedURLException {
        URL localUrl = new URL(url);

         host = localUrl.getHost();

         protocol=localUrl.getProtocol();
         file=localUrl.getFile();
         port=localUrl.getPort();
         if (port==-1){
             port=localUrl.getDefaultPort();
         }

        if(TextUtils.isEmpty(file)){
            //如果为空，默认加上"/"
            file = "/";
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
