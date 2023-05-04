package com.netty.rpc.registry;

import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 注册中心
 * @date 2023/5/2 20:08
 * @author: qyl
 */
public abstract class ServiceRegistry {
    protected ConcurrentHashMap<ServiceDescriptor, ServiceInstance> services = new ConcurrentHashMap<> ();

    /**
     * 根据请求 进行服务查找
     *
     * @param request
     * @param <T>
     * @return
     */
    public  <T> T lookup(RpcRequest request) {
        ServiceDescriptor desc = request.getServiceDescriptor ();
        return (T) services.getOrDefault (desc, null);
    }


    /**
     * 把某个服务提供者的所有服务进行注册
     * @param host
     * @param port
     * @param services
     * @throws Exception
     */
    public abstract void register(String host, int port, Map<ServiceDescriptor, ServiceInstance> services) throws Exception;
}
