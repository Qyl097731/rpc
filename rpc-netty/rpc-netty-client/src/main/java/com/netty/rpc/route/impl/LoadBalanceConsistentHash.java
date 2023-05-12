package com.netty.rpc.route.impl;

import com.google.common.hash.Hashing;
import com.netty.rpc.route.LoadBalance;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;

import java.util.List;

/**
 * @description 一致性hash 负载均衡
 * @date 2023/5/11 23:26
 * @author: qyl
 */
public class LoadBalanceConsistentHash implements LoadBalance {
    @Override
    public RpcPeer doRoute(ServiceDescriptor service, List<RpcPeer> serviceProviders) {
        int index = Hashing.consistentHash (service.hashCode (),serviceProviders.size ());
        return serviceProviders.get (index);
    }
}
