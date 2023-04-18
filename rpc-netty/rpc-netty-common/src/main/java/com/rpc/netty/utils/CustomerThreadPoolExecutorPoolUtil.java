package com.rpc.netty.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 自定义线城市
 *
 * @author qyl
 */
public class CustomerThreadPoolExecutorPoolUtil {
    private static ExecutorService pool = null;
    private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("rpc-netty-%d").build();
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private CustomerThreadPoolExecutorPoolUtil() {}

    public static void destroy() {
        if (pool != null) {
            pool.shutdown();
        }
    }

    public static ExecutorService getThreadPoolExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                factory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
