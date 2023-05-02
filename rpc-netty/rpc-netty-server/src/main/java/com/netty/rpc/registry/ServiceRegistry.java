package com.netty.rpc.registry;

import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 注册中心
 * @date 2023/5/2 20:08
 * @author: qyl
 */
public abstract class ServiceRegistry {
    protected static ConcurrentHashMap<ServiceDescriptor, ServiceInstance> services = new ConcurrentHashMap<> ();

    public abstract <T, P> void register(Class<T> clazz, P instance);

    public abstract <T> T lookup(RpcRequest request);
}
