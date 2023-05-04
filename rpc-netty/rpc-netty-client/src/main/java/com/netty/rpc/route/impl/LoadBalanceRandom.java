package com.netty.rpc.route.impl;

import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.LoadBalance;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description 随机负载均衡
 * @date 2023/4/12 21:44
 * @author: qyl
 */
public class LoadBalanceRandom implements LoadBalance {
    @Override
    public RpcPeer route(ServiceDescriptor service, Map<RpcPeer, RpcClientHandler> connectionPool) throws Exception {
        Map<ServiceDescriptor, List<RpcPeer>> serverProviderMap = revertProviderMap(connectionPool);
        if (serverProviderMap != null) {
            List<RpcPeer> serviceProviders = serverProviderMap.get(service);
            if (CollectionUtils.isNotEmpty(serviceProviders)) {
              return doRoute(serviceProviders);
            }
        }
        throw new Exception("Can not find connection for service: " + service.getClazz() + "." + service.getMethod() + "#" + service.getVersion());
    }

    private RpcPeer doRoute(List<RpcPeer> serviceProviders) {
        int size = serviceProviders.size();
        int random = ThreadLocalRandom.current().nextInt(size);
        return serviceProviders.get(random);
    }
}
