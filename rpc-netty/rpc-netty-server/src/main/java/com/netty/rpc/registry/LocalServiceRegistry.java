package com.netty.rpc.registry;

import com.rpc.netty.protocol.ServiceDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
* @description 本地内存模拟注册中心
* @date 2023/5/2 20:18
* @author: qyl
*/
@Slf4j
public class LocalServiceRegistry extends ServiceRegistry{
    /**
     * 注册RpcService标注的类中的所有方法或者RpcService标注的方法
     * @param host
     * @param port
     * @param services
     */
    @Override
    public void register(String host, int port, Map<ServiceDescriptor, ServiceInstance> services) {
        this.services.putAll (services);
        log.info("Register {} new service, host: {}, port: {}", services.size(), host, port);
    }
}
