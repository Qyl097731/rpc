package com.netty.rpc.registry;

import com.rpc.netty.annotation.RpcService;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.AnnotationUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
* @description 本地内存模拟注册中心
* @date 2023/5/2 20:18
* @author: qyl
*/
@Slf4j
public class LocalServiceRegistry extends ServiceRegistry{
    /**
     * @param clazz
     * @param instance
     * @param <T>
     * @param <P>
     */
    @Override
    public <T, P> void register(Class<T> clazz, P instance) {
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
    public  <T> T lookup(RpcRequest request) {
        ServiceDescriptor desc = request.getServiceDescriptor ();
        return (T) services.getOrDefault (desc, null);
    }
}
