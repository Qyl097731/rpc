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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 服务注册中心
 * @date 2023/4/11 0:41
 * @author: qyl
 */
@Slf4j
public class ServiceManager {
    // 所选择的注册器
    private static ServiceRegistry serviceRegistry;
    // 当前服务提供者的ip和端口
    private static String host;
    private static Integer port;

    private ServiceManager() {}

    private static class ServiceHolder {
        private static final ServiceManager instance = new ServiceManager ();
    }

    public static ServiceManager getInstance(String host,int port) {
        return getInstance (host,port,new LocalServiceRegistry ());
    }

    public static ServiceManager getInstance(String serverHost,int serverPort,ServiceRegistry registry) {
        host = serverHost;
        port = serverPort;
        serviceRegistry = registry;
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
    public void registerServices(String[] basePackages) throws Exception {
        List<Class<?>> classes = AnnotationUtils.getServicesClasses (basePackages);
        HashMap<ServiceDescriptor, ServiceInstance> services = new HashMap<> ();
        for (Class<?> clazz : classes) {
            services.putAll (register (clazz.getInterfaces ()[0], clazz.newInstance ()));
        }
        serviceRegistry.register (host,port,services);
    }

    /**
     * 注册RpcService标注的类中的所有方法或者RpcService标注的方法
     *
     * @param clazz    接口类
     * @param instance 实现类实例
     * @param <T>      接口类型
     * @param <P>      实现类类型
     * @return services服务
     */
    public <T, P> Map<? extends ServiceDescriptor, ? extends ServiceInstance> register(Class<T> clazz, P instance) {
        HashMap<ServiceDescriptor, ServiceInstance> services = new HashMap<> ();
        Method[] methods = AnnotationUtils.getExplodedMethods (instance.getClass ());
        RpcService classAnnotation = null, methodAnnotation = null;
        if (instance.getClass ().isAnnotationPresent (RpcService.class)) {
            classAnnotation = instance.getClass ().getAnnotation (RpcService.class);
        }
        for (Method method : methods) {
            String version = method.isAnnotationPresent (RpcService.class) ?
                    method.getAnnotation (RpcService.class).version () : classAnnotation.version ();
            ServiceDescriptor descriptor = ServiceDescriptor.of (clazz, method, version);
            ServiceInstance service = new ServiceInstance (instance, method);
            services.put (descriptor, service);
        }
        return services;
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
