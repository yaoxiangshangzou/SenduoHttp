package com.example.huajietest.http.chain;

import com.example.huajietest.http.Response;

import java.io.IOException;


/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/5/5
 * * 文件描述：拦截器接口
 * * 修改历史：2018/5/5 17:04*************************************
 **/

public interface Interceptor {

    Response intercept(InterceptorChain interceptorChain) throws IOException;
}
