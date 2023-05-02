package com.rpc.netty.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @description zookeeper客户端
 * @date 2023/5/2 14:42
 * @author: qyl
 */
public class CuratorClient {
    private CuratorFramework client;

    public CuratorClient(){
        RetryPolicy retryPolicy  = new ExponentialBackoffRetry (1000,3);
        client = CuratorFrameworkFactory.builder ()
                .connectString ("server:2181")
                .sessionTimeoutMs (3000)
                .connectionTimeoutMs (5000)
                .retryPolicy (retryPolicy)
                .build ();
        client.start ();
    }

    public String createPathData(String path, byte[] data) throws Exception {
        return client.create ().creatingParentsIfNeeded ()
                .withMode (CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath (path,data);
    }
}
