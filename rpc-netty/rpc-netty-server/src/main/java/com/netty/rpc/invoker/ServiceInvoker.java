package com.netty.rpc.invoker;

import com.netty.rpc.registry.ServiceInstance;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.utils.ReflectionUtils;

/**
 * @description 服务调用类
 * @date 2023/4/11 14:47
 * @author: qyl
 */
public class ServiceInvoker {
    public static Object invoke(ServiceInstance service, RpcRequest request) {
        return ReflectionUtils.invoke (service.getTarget (), service.getMethod (), request.getParameters ());
    }
}
