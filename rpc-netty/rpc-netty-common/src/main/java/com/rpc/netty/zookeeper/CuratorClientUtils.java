package com.rpc.netty.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @description zookeeper客户端
 * @date 2023/5/2 14:42
 * @author: qyl
 */
public class CuratorClientUtils {
    private static CuratorFramework client;

    public static void createClient() throws InterruptedException {
        RetryPolicy retryPolicy  = new ExponentialBackoffRetry (1000,3);
        client = CuratorFrameworkFactory.builder ()
                .connectString ("server:2181")
                .sessionTimeoutMs (3000)
                .connectionTimeoutMs (5000)
                .retryPolicy (retryPolicy)
                .build ();
        client.start ();
        client.blockUntilConnected();
    }

}
