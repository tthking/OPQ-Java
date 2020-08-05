package cn.lliiooll.iotqq.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskUtils {
    public static ExecutorService create(String s) {
        return create(0L, s);
    }

    public static ExecutorService create(long keepAliveTime, String s) {
        return create(keepAliveTime, 4, s);
    }

    public static ExecutorService create(long keepAliveTime, int corePoolSize, String s) {
        return create(keepAliveTime, corePoolSize, 50, s);
    }

    public static ExecutorService create(long keepAliveTime, int corePoolSize, int maximumPoolSize, String s) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat(s).build(), new ThreadPoolExecutor.AbortPolicy());
    }

}
