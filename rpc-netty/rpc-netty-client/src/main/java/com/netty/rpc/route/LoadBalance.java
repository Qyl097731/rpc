package com.netty.rpc.route;

import com.netty.rpc.handler.RpcClientHandler;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description 负载均衡类
 * @date 2023/4/12 21:43
 * @author: qyl
 */
public interface LoadBalance {
    /**
     * 返回线程池中的连接
     *
     * @param pool
     * @return
     */
    RpcClientHandler choose(CopyOnWriteArrayList<RpcClientHandler> pool);
}
