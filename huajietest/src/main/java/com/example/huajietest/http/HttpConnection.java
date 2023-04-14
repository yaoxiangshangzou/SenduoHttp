package com.example.huajietest.http;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * @author :      SunHuaJie
 * @date :        2023/4/11-23:43
 * @email :       1214495199@qq.com
 * @description : 创建与服务器连接的socket
 */
public class HttpConnection {
    Socket socket;
    long lastUseTime;
    private Request request;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void setRequest(Request request) {
        this.request = request;
    }

    public void updateLastUseTime() {
        lastUseTime = System.currentTimeMillis();
    }

    public boolean isSameAddRess(String host, int port) {
        if (socket == null) {
            return false;
        }
        return TextUtils.equals(request.getHttpUrl().getHost(), host) && request.getHttpUrl().port == port;
    }

    /**
     * 创建socket连接
     * @throws IOException
     */
    private void createSocket() throws IOException {
        if (socket == null || socket.isClosed()) {
            HttpUrl httpUrl = request.getHttpUrl();
            if (httpUrl.protocol.equalsIgnoreCase(HttpCodec.PROTOCOL_HTTPS)) {
                socket = SSLSocketFactory.getDefault().createSocket();
            } else {
                socket = new Socket();
            }
            socket.connect(new InetSocketAddress(httpUrl.getHost(), httpUrl.getPort()));
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
    }

    public void close(){
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public InputStream call(HttpCodec httpCodec) throws IOException {
        createSocket();
        httpCodec.writeRequest(outputStream,request);
        return inputStream;
    }
}
