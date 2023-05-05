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
                .connectString ("master:2181")
                .sessionTimeoutMs (3000)
                .connectionTimeoutMs (5000)
                .retryPolicy (retryPolicy)
                .build ();
        client.start ();
    }

    /**
     * 临时服务节点注册
     * @param path
     * @param data
     * @return 注册路径
     * @throws Exception
     */
    public String createPathData(String path, byte[] data) throws Exception {
        return client.create ().creatingParentsIfNeeded ()
                .withMode (CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath (path, data);
    }

    /**
     * 获取指定的路径下的直系节点
     * @param path
     * @return 直接孩子集合
     * @throws Exception
     */
    public List<String> getChildren(String path) throws Exception {
        return client.getChildren ().forPath (path);
    }

    /**
     * 获取指定路径下的节点数据
     * @param path
     * @return 节点数据
     * @throws Exception
     */
    public byte[] getData(String path) throws Exception {
        return client.getData ().forPath (path);
    }

    public void reWatchChildNods(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache (client, path, true);
        childrenCache.start (PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        childrenCache.getListenable ().addListener (listener);
    }
}
