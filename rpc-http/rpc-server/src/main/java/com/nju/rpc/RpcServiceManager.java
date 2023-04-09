package com.nju.rpc;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 服务管理，负责服务的暴露
 * @date 2023/4/9 17:21
 * @author: qyl
 */
@Slf4j
public class RpcServiceManager {
    private ConcurrentHashMap<ServiceDescriptor, RpcServiceInstance> serviceMap;

    public RpcServiceManager() {
        serviceMap = new ConcurrentHashMap<> ();
    }

    /**
     * 把服务都进行注册
     *
     * @param <T>
     * @param object
     */
    public <T> void register(Class<T> object,T bean) {
        Method[] methods = ReflectionUtils.getPublicMethods (object);
        for (Method method : methods) {
            ServiceDescriptor descriptor = ServiceDescriptor.from (object, method);
            RpcServiceInstance serviceInstance = new RpcServiceInstance(bean,method);
            serviceMap.put (descriptor,serviceInstance);
            log.info ("register service: {} {}", descriptor.getClazz (),descriptor.getMethod ());
        }
    }

    public  RpcServiceInstance lookup(Request request){
        ServiceDescriptor descriptor = request.getService ();
        return serviceMap.get (descriptor);
    }
}
