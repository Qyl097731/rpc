package com.netty.rpc.manager;

import com.rpc.netty.annotation.RpcService;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
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
