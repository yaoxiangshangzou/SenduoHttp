package com.example.huajietest.http.chain;


import android.util.Log;

import com.example.huajietest.http.HttpClient;
import com.example.huajietest.http.HttpConnection;
import com.example.huajietest.http.HttpUrl;
import com.example.huajietest.http.Request;
import com.example.huajietest.http.Response;
import com.example.huajietest.http.chain.Interceptor;

import java.io.IOException;


/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/5/5
 * * 文件描述：获得有效连接的socket的拦截器
 * * 修改历史：2018/5/5 22:10*************************************
 **/

public class ConnectionInterceptor implements com.example.huajietest.http.chain.Interceptor {
    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.e("interceptor", "获取连接拦截器");
        Request request = interceptorChain.call.getRequest();
        HttpClient httpClient = interceptorChain.call.getHttpClient();
        HttpUrl httpUrl = request.getHttpUrl();

        HttpConnection httpConnection = httpClient.getConnectionPool().getHttpConnection(httpUrl.getHost(),httpUrl.getPort());
        if(null == httpConnection){
            httpConnection = new HttpConnection();
        }else{
            Log.e("interceptor", "从连接池中获得连接");
        }
        httpConnection.setRequest(request);

        try {
            Response response = interceptorChain.proceed(httpConnection);
            if (response.isKeepAlive()){
                httpClient.getConnectionPool().putHttpConnection(httpConnection);
            }else{
                httpConnection.close();
            }
            return response;
        }catch (IOException e){
            httpConnection.close();
            throw e;
        }
    }
}
