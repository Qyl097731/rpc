package com.netty.rpc.handler;

import com.netty.rpc.connect.ConnectionManager;
import com.rpc.netty.codec.RpcFuture;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.protocol.ServiceDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

/**
 * @description 中转站，封装要调用的服务 成为 Request
 * @date 2023/4/11 21:23
 * @author: qyl
 */
@Slf4j
public class RemoteInvocationHandler implements InvocationHandler {
    private Class target;
    private String version;

    public <T> RemoteInvocationHandler(Class<T> clazz, String version) {
        this.target = clazz;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            RpcRequest request = new RpcRequest ();
            request.setServiceDescriptor (ServiceDescriptor.of (target, method,version));
            request.setParameters (args);
            return invokeRemote (request);
        }catch (Exception e) {
            log.error("调用远程{}#{}失败...{}", proxy,method.getName(),e);
        }
        return null;
    }

    private Object invokeRemote(RpcRequest request) throws ExecutionException, InterruptedException {
        ConnectionManager manager = ConnectionManager.getInstance ();
        RpcClientHandler handler = manager.borrow ();
        RpcFuture future = handler.send (request);
        manager.release (handler);
        return future.get ();
    }
}
