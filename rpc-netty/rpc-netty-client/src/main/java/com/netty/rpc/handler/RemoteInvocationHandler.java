package com.netty.rpc.handler;

import com.netty.rpc.connect.ConnectionManager;
import com.rpc.netty.codec.RpcFuture;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @description 中转站，封装要调用的服务 成为 Request
 * @date 2023/4/11 21:23
 * @author: qyl
 */
@Slf4j
public class RemoteInvocationHandler implements InvocationHandler {
    private final Class target;
    private final String version;

    public <T> RemoteInvocationHandler(Class<T> clazz, String version) {
        this.target = clazz;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            if (isCallRemote(method)){
                RpcRequest request = new RpcRequest ();
                request.setServiceId (UUID.randomUUID().toString().replaceAll ("-",""));
                request.setServiceDescriptor (ServiceDescriptor.of (target, method,version));
                request.setParameters (args);
                return invokeRemote (request);
            }else {
                return invokeLocal (proxy,method,args);
            }
        }catch (Exception e) {
            log.error("调用远程{}#{}失败...{}", target.getSimpleName (),method.getName(),e.getMessage ());
        }
        return null;
    }

    private boolean isCallRemote(Method method) {
        return !method.getDeclaringClass ().equals(Object.class );
    }

    private Object invokeLocal(Object proxy, Method method, Object[] args) {
        if (method.getDeclaringClass ().equals (Object.class)){
            return ReflectionUtils.invoke (ReflectionUtils.create (Object.class), method);
        }else {
            return null;
        }
    }

    private Object invokeRemote(RpcRequest request) throws Exception {
        ConnectionManager manager = ConnectionManager.getInstance ();
        RpcClientHandler handler = manager.borrow (request);
        RpcFuture future = handler.send (request);
        return future.get ();
    }
}
