package com.example.huajietest.http;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author :      SunHuaJie
 * @date :        2023/4/12-11:19
 * @email :       1214495199@qq.com
 * @description :
 */
public class ConnectionPool {
    private static final String TAG = "ConnectionPool";
    private long keepAliveTime;

    private Deque<HttpConnection> httpConnections = new ArrayDeque<>();

    private boolean cleanupRunning;

    private static final Executor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread thread = new Thread(runnable, "this is connection pool");
            thread.setDaemon(true);//设为守护线程
            return thread;
        }
    });

    public ConnectionPool() {
        this(60L, TimeUnit.SECONDS);
    }

    public ConnectionPool(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = timeUnit.toMillis(keepAliveTime);
    }

    //生成一个清理线程, 这个线程会定期去检查, 并且清理哪些无用的连接,这里的无用是指没使用的期间超过了保留时间
    private Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long now = System.currentTimeMillis();
                long waitDuration = cleanup(now);//获取到下次检查时间
                if (waitDuration == -1) {
                    return;//连接池为空,清理线程执行结束
                }


                if (waitDuration > 0) {
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(waitDuration);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    /**
     * 根据当前时间,清理无用的连接
     *
     * @param now
     * @return
     */
    private long cleanup(long now) {
        long longestIdleDuration = -1;
        synchronized (this) {
            Iterator<HttpConnection> iterator = httpConnections.iterator();
            while (iterator.hasNext()) {
                HttpConnection httpConnection = iterator.next();
                //计算闲置时间
                long idleDuration = now - httpConnection.lastUseTime;
                if (idleDuration > keepAliveTime) {
                    iterator.remove();
                    httpConnection.close();

                    Log.d(TAG, "超过闲置时间,移出连接池");
                    continue;
                }

                //然后就整个连接池中最大的闲置时间
                if (idleDuration > longestIdleDuration) {
                    longestIdleDuration = idleDuration;
                }
            }
            if (longestIdleDuration >= 0) {
                return keepAliveTime - longestIdleDuration;//这里返回的值,可以让清理线程知道,下一次要多久之后
            }

            //如果运行到这里的话, 代表longestIdleDuration=-1 连接池中为空
            cleanupRunning = false;
            return longestIdleDuration;
        }
    }

    public void putHttpConnection(HttpConnection httpConnection) {
        if (!cleanupRunning) {
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        httpConnections.add(httpConnection);
    }

    public synchronized HttpConnection getHttpConnection(String host, int port) {
        Iterator<HttpConnection> iterator = httpConnections.iterator();
        while (iterator.hasNext()) {
            HttpConnection httpConnection = iterator.next();
            if (httpConnection.isSameAddRess(host, port)) {
                iterator.remove();
                return httpConnection;
            }
        }

        return null;
    }
}
