package com.netty.rpc.registry;

import com.rpc.netty.annotation.RpcService;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @description 本地内存模拟注册中心
* @date 2023/5/2 20:18
* @author: qyl
*/
@Slf4j
public class LocalServiceRegistry extends ServiceRegistry{
    private ConcurrentHashMap<ServiceDescriptor, ServiceInstance> services = new ConcurrentHashMap<> ();

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
}
