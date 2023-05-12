package com.netty.rpc.route.impl;

import com.netty.rpc.route.LoadBalance;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description 轮询算法 简单防止越界
 * @date 2023/5/11 20:34
 * @author: qyl
 */
public class LoadBalanceRoundRobin implements LoadBalance {
    private AtomicInteger sequence = new AtomicInteger(0);
    private static final Integer BOUNDARY = Integer.MAX_VALUE - 10000;
    @Override
    public RpcPeer doRoute(ServiceDescriptor service,List<RpcPeer> serviceProviders) {
        int serverCount = serviceProviders.size();
        if (serverCount == 0) {
            throw new IllegalStateException("No servers available");
        }
        int index = (sequence.getAndIncrement() % serverCount);
        while (sequence.get () >= BOUNDARY){
            sequence.compareAndSet (sequence.get (),0);
        }
        return serviceProviders.get (index);
    }
}
