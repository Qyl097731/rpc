package com.netty.rpc.route.impl;

import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.LoadBalance;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description 随机负载均衡
 * @date 2023/4/12 21:44
 * @author: qyl
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public RpcClientHandler choose(CopyOnWriteArrayList<RpcClientHandler> pool) {
        int size = pool.size ();
        int random = ThreadLocalRandom.current().nextInt(size);
        return pool.get(random);
    }
}
