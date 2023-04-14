package com.example.huajietest.http.chain;

import android.util.Log;

import com.example.huajietest.http.HttpCodec;
import com.example.huajietest.http.Request;
import com.example.huajietest.http.Response;

import java.io.IOException;
import java.util.Map;


/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/5/5
 * * 文件描述：
 * * 修改历史：2018/5/5 18:11*************************************
 **/

public class HeadersInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {

        Log.e("interceprot","Http头拦截器....");

        Request request = interceptorChain.call.getRequest();
        Map<String,String> headers = request.getHeaders();
        if(!headers.containsKey(HttpCodec.HEAD_HOST)){
            headers.put(HttpCodec.HEAD_HOST,request.getHttpUrl().getHost());
        }
        if(!headers.containsKey(HttpCodec.HEAD_CONNECTION)) {
            headers.put(HttpCodec.HEAD_CONNECTION, HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        }

        if(null != request.getRequestBody()){
            String contentType = request.getRequestBody().getContentType();
            if(null != contentType){
                headers.put(HttpCodec.HEAD_CONTENT_TYPE,contentType);
            }

            long contentLength = request.getRequestBody().getContentLength();

            if(-1 != contentLength){
                headers.put(HttpCodec.HEAD_CONTENT_LENGTH,Long.toString(contentLength));
            }
        }
        return interceptorChain.proceed();
    }
}
