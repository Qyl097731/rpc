package com.netty.rpc.route.impl;

import com.netty.rpc.route.LoadBalance;
import com.rpc.netty.protocol.RpcPeer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description 随机负载均衡
 * @date 2023/4/12 21:44
 * @author: qyl
 */
public class LoadBalanceRandom implements LoadBalance {
    @Override
    public RpcPeer doRoute(List<RpcPeer> serviceProviders) {
        int size = serviceProviders.size();
        int random = ThreadLocalRandom.current().nextInt(size);
        return serviceProviders.get(random);
    }
}
