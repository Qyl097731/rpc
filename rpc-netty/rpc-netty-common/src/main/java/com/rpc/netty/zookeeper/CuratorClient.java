package com.rpc.netty.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @description zookeeper客户端
 * @date 2023/5/2 14:42
 * @author: qyl
 */
public class CuratorClient {
    private CuratorFramework client;

    public CuratorClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry (1000, 3);
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
                .forPath (path, data);
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren ().forPath (path);
    }


    public byte[] getData(String path) throws Exception {
        return client.getData ().forPath (path);
    }

    public void reWatchChildNods(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache (client, path, true);
        childrenCache.start (PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        childrenCache.getListenable ().addListener (listener);
    }
}
