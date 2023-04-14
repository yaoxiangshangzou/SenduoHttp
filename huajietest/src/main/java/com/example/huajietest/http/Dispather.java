package com.example.huajietest.http;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author :      SunHuaJie
 * @date :        2023/4/11-21:55
 * @email :       1214495199@qq.com
 * @description : 网络请求任务调度器   通过线程池来控制
 */
public class Dispather {
    //最多同时请求的数量
    private int maxRequests;
    //同一个host最多允许请求的数量
    private int maxRequestPreHost;

    public Dispather() {
        this(64, 5);
    }

    public Dispather(int maxRequests, int maxRequestPreHost) {
        this.maxRequestPreHost = maxRequestPreHost;
        this.maxRequests = maxRequests;
    }

    private ExecutorService executorService;

    //等待双端队列  双端比较适合增加与删除
    private final Deque<Call.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    //运行中的 双端队列
    private final Deque<Call.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    public synchronized ExecutorService initExecetorService() {
        if (executorService == null) {
            //这里只是给这个线程起一个名字
            ThreadFactory threadFactory = new ThreadFactory() {

                @Override
                public Thread newThread(@NonNull Runnable runnable) {
                    Thread thread = new Thread(runnable, "Http Client Thread");
                    return thread;
                }
            };
            //这里按照OKhttp 的线程池样式来创建,单个线程在闲事保留60s
            executorService = new ThreadPoolExecutor(
                    0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), threadFactory
            );

        }
        return executorService;
    }

    public void enqueue(Call.AsyncCall asyncCall) {
        Log.e("Dispatcher", "同时有:" + runningAsyncCalls.size());
        Log.e("Dispatcher", "host同时有:" + getRunningPreHostCount(asyncCall));
        //TODO 首先判断正在运行的队列是否已经满了，而且同一个host请求的是否已经超过规定的数量
        if (runningAsyncCalls.size() < maxRequests && getRunningPreHostCount(asyncCall) < maxRequestPreHost) {
            Log.e("Dispatcher", "提交执行");
            runningAsyncCalls.add(asyncCall);
            initExecetorService().execute(asyncCall);
        } else {
            //不满足条件，就加到等待队列
            Log.e("Dispatcher", "等待执行");
            readyAsyncCalls.add(asyncCall);
        }

    }

    /**
     * 获取同一个host在正在运行队列中的数量
     *
     * @param asyncCall
     * @return
     */
    private int getRunningPreHostCount(Call.AsyncCall asyncCall) {
        int count = 0;
        for (Call.AsyncCall runningAsyncCall : runningAsyncCalls) {
            if (runningAsyncCall.getHost().equals(asyncCall.getHost())) {
                count++;
            }
        }
        return count;
    }

    public void finished(Call.AsyncCall asyncCall) {
        synchronized (this) {
            runningAsyncCalls.remove(asyncCall);
            checkReadyCalls();
        }
    }

    /**
     * 检查是否可以运行等待中的请求
     */
    private void checkReadyCalls() {
        //达到了同时请求最大数
        if (runningAsyncCalls.size() >= maxRequests) {
            return;
        }
        //没有等待执行的任务
        if (readyAsyncCalls.isEmpty()) {
            return;
        }

        Iterator<Call.AsyncCall> iterator = readyAsyncCalls.iterator();
        while (iterator.hasNext()) {
            Call.AsyncCall asyncCall = iterator.next();
            //如果获得的等待执行的任务  执行后 小于host相同最大允许数  就可以去执行
            if (getRunningPreHostCount(asyncCall) < maxRequestPreHost) {
                iterator.remove();
                runningAsyncCalls.add(asyncCall);
                executorService.execute(asyncCall);
            }

            if (runningAsyncCalls.size() >= maxRequests) {
                return;
            }
        }
    }
}
