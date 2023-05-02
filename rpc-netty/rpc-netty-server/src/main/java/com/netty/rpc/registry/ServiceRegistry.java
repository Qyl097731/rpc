package com.netty.rpc.registry;

import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import java.util.Map;

/**
 * @description 注册中心
 * @date 2023/5/2 20:08
 * @author: qyl
 */
public abstract class ServiceRegistry {
    /**
     * 服务发现
     * @param request
     * @return
     * @param <T>
     */
    public abstract <T> T lookup(RpcRequest request);

    /**
     * 把某个服务提供者的所有服务进行注册
     * @param host
     * @param port
     * @param services
     */
    public abstract void register(String host, int port, Map<ServiceDescriptor, ServiceInstance> services) throws Exception;
}
