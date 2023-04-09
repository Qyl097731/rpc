package com.nju.rpc;

/**
 * @description 服务调用
 * @date 2023/4/9 18:14
 * @author: qyl
 */
public class ServiceInvoker {
    public Object invoke(RpcServiceInstance service, Request request) {
        return ReflectionUtils.invoke (service.getTarget (), service.getMethod (), request.getParameters ());
    }
}
