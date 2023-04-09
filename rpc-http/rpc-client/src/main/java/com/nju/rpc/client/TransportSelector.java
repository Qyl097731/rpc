package com.nju.rpc.client;

import com.nju.rpc.Peer;
import com.nju.transporter.TransportClient;

import java.util.List;

/**
 * @description 选择哪个Server连接
 * @date 2023/4/9 18:41
 * @author: qyl
 */
public interface TransportSelector {
    /**
     * 初始化连接池
     * @param peers 可以连接的server端点信息
     * @param count client与各个server建立多少个连接
     * @param clazz client实现类
     */
    void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz);

    /**
     * 选择一个transport和server交互
     *
     * @return 网络client
     */
    TransportClient select();

    /**
     * 释放用完client
     *
     * @param client
     */
    void release(TransportClient client);

    /**
     * 关闭连接池
     */
    void close();
}
