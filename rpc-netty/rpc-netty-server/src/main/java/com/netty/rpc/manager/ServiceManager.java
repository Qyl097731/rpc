package com.netty.rpc.manager;

import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 服务注册中心
 * @date 2023/4/11 0:41
 * @author: qyl
 */
@Slf4j
public class ServiceManager {
    private ServiceManager() {
    }

    private static class ServiceHolder {
        private static final ConcurrentHashMap<ServiceDescriptor, ServiceInstance> singletonInstance =
                new ConcurrentHashMap<> ();
    }

    private static ConcurrentHashMap<ServiceDescriptor, ServiceInstance> services;

    public static void init() {
        services = ServiceHolder.singletonInstance;
    }

    public static <T> void register(Class<T> clazz, T instance) {
        Method[] methods = ReflectionUtils.getPublicMethods (clazz);
        for (Method method : methods) {
            ServiceDescriptor descriptor = ServiceDescriptor.of (clazz, method);
            ServiceInstance service = new ServiceInstance (instance, method);
            services.put (descriptor, service);
            log.info ("{} {} success to register ", instance.getClass ().getName (), method.getName ());
        }
    }

    /**
     * 根据请求 进行服务查找
     * @param request
     * @return
     * @param <T>
     */
    public static <T> T lookup(RpcRequest request) {
        ServiceDescriptor desc = request.getServiceDescriptor ();
        return (T)services.getOrDefault(desc, null);
    }
}
