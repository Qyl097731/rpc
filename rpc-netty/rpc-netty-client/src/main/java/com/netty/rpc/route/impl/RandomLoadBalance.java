package com.netty.rpc.route.impl;

import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.LoadBalance;

import java.util.concurrent.BlockingQueue;

/**
 * @description 随机负载均衡
 * @date 2023/4/12 21:44
 * @author: qyl
 */
public class RandomLoadBalance implements LoadBalance {
    // TODO 后面完善，现在假装随机
    @Override
    public RpcClientHandler take(BlockingQueue<RpcClientHandler> pool) {
        RpcClientHandler handler = null;
        try {
            handler = pool.take ();
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
        return handler;
    }
}
