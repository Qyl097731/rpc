package com.netty.rpc.route;

import com.netty.rpc.handler.RpcClientHandler;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description 负载均衡类
 * @date 2023/4/12 21:43
 * @author: qyl
 */
public interface LoadBalance {
    /**
     * 真正的负载均衡，通过ZK注册的多个Provider中获取一个Provider
     *
     * @param service
     * @param connectionPool
     * @return handler处理器
     * @throws Exception 没有相应的服务
     */
    default RpcPeer route(ServiceDescriptor service, Map<RpcPeer, RpcClientHandler> connectionPool) throws Exception {
        Map<ServiceDescriptor, List<RpcPeer>> serverProviderMap = revertProviderMap (connectionPool);
        if (serverProviderMap != null) {
            List<RpcPeer> serviceProviders = serverProviderMap.get (service);
            if (CollectionUtils.isNotEmpty (serviceProviders)) {
                return doRoute (serviceProviders);
            }
        }
        return null;
    }

    /**
     * 将<服务提供者,处理器>的map变成<服务，多个服务提供者节点>，以便真正负载均衡
     *
     * @param connectionPool
     * @return <服务，多个服务提供者节点> map
     */
    default Map<ServiceDescriptor, List<RpcPeer>> revertProviderMap(Map<RpcPeer, RpcClientHandler> connectionPool) {
        Map<ServiceDescriptor, List<RpcPeer>> serverProviderMap = new HashMap<> ();
        for (Map.Entry<RpcPeer, RpcClientHandler> entry : connectionPool.entrySet ()) {
            RpcPeer peer = entry.getKey ();
            List<ServiceDescriptor> services = peer.getServices ();
            if (!CollectionUtils.isEmpty (services)) {
                for (ServiceDescriptor service : services) {
                    List<RpcPeer> serversProviders = serverProviderMap.get (service);
                    if (serversProviders == null) {
                        serversProviders = new ArrayList<> ();
                    }
                    serversProviders.add (peer);
                    serverProviderMap.putIfAbsent (service, serversProviders);
                }
            }
        }
        return serverProviderMap;
    }

    /**
     * 负载均衡接口方法 Round Robin 、 Random
     * @param serviceProviders
     * @return 返回选择的服务器
     */
    default RpcPeer doRoute(List<RpcPeer> serviceProviders){
        throw new UnsupportedOperationException("不支持该服务");
    }

    /**
     * 负载均衡接口方法 LFU
     * @param serviceName
     * @param serviceProviders
     * @return 返回选择的服务器
     */
    default RpcPeer doRoute(String serviceName,List<RpcPeer> serviceProviders){
        throw new UnsupportedOperationException("不支持该服务");
    }
}
