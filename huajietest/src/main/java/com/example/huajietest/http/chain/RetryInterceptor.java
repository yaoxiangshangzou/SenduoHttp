package com.example.huajietest.http.chain;

import android.util.Log;

import com.example.huajietest.http.Call;
import com.example.huajietest.http.Response;

import java.io.IOException;


/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/5/5
 * * 文件描述：
 * * 修改历史：2018/5/5 17:22*************************************
 **/

public class RetryInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.e("interceprot", "重试拦截器....");
        Call call = interceptorChain.call;
        IOException ioException = null;

        for(int i = 0 ; i < call.getHttpClient().getRetryTimes(); i ++){

            if(call.isCanceled()){
                throw new IOException("this task had canceled");
            }

            try {
                Response response = interceptorChain.proceed();
                return response;
            }catch (IOException e){
                ioException = e;
            }
        }
        throw ioException;
    }
}
