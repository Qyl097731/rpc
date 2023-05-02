package com.netty.rpc.manager;

import com.netty.rpc.registry.LocalServiceRegistry;
import com.netty.rpc.registry.ServiceInstance;
import com.netty.rpc.registry.ServiceRegistry;
import com.rpc.netty.annotation.RpcService;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.AnnotationUtils;
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
    private static ServiceRegistry serviceRegistry = new LocalServiceRegistry ();

    private ServiceManager() {}

    private static class ServiceHolder {
        private static final ServiceManager instance = new ServiceManager ();
    }

    public static ServiceManager getInstance() {
        return ServiceHolder.instance;
    }

    public static ServiceManager getInstance(ServiceRegistry registry) {
        ServiceManager.serviceRegistry = registry;
        return ServiceHolder.instance;
    }

    /**
     * 获取指定包下的所有RpcService标注的类
     *
     * @param basePackages
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void registerServices(String[] basePackages) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Class<?>> classes = AnnotationUtils.getServicesClasses (basePackages);
        for (Class<?> clazz : classes) {
            register (clazz.getInterfaces ()[0], clazz.newInstance ());
        }
    }

    /**
     * 注册RpcService标注的类中的所有方法或者RpcService标注的方法
     *
     * @param clazz    接口类
     * @param instance 实现类实例
     * @param <T>      接口类型
     * @param <P>      实现类类型
     */
    public static <T, P> void register(Class<T> clazz, P instance) {
        serviceRegistry.register(clazz, instance);
    }

    /**
     * 根据请求 进行服务查找
     *
     * @param request
     * @param <T>
     * @return
     */
    public static <T> T lookup(RpcRequest request) {
        return serviceRegistry.lookup (request);
    }
}
