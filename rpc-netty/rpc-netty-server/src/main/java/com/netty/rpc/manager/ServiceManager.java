package com.netty.rpc.manager;

import com.rpc.netty.annotation.RpcService;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.AnnotationUtils;
import com.rpc.netty.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 服务注册中心
 * @date 2023/4/11 0:41
 * @author: qyl
 */
@Slf4j
public class ServiceManager {
    private static ConcurrentHashMap<ServiceDescriptor, ServiceInstance> services;

    private ServiceManager() {
        services = new ConcurrentHashMap<> ();
    }

    private static class ServiceHolder {
        private static final ServiceManager instance = new ServiceManager ();
    }

    public static ServiceManager getInstance() {
        return ServiceHolder.instance;
    }

    public void registerServices(String[] basePackages) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Class<?>> classes = AnnotationUtils.getServicesClasses (basePackages);
        for (Class<?> clazz : classes) {
            register (clazz.getInterfaces ()[0], clazz.newInstance (), clazz.isAnnotationPresent (RpcService.class));
        }
    }

    public static <T, P> void register(Class<T> clazz, P instance, boolean isAll) {
        Method[] methods = AnnotationUtils.getExplodedMethods (clazz, isAll);
        for (Method method : methods) {
            ServiceDescriptor descriptor = ServiceDescriptor.of (clazz, method);
            ServiceInstance service = new ServiceInstance (instance, method);
            services.put (descriptor, service);
            log.info ("{} {} success to register ", instance.getClass ().getName (), method.getName ());
        }
    }

    /**
     * 根据请求 进行服务查找
     *
     * @param request
     * @param <T>
     * @return
     */
    public static <T> T lookup(RpcRequest request) {
        ServiceDescriptor desc = request.getServiceDescriptor ();
        return (T) services.getOrDefault (desc, null);
    }
}
