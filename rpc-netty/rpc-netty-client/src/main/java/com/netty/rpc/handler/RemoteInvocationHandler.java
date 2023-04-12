package com.netty.rpc.handler;

import com.netty.rpc.connect.ConnectionManager;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.protocol.ServiceDescriptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @description 中转站，封装要调用的服务 成为 Request
 * @date 2023/4/11 21:23
 * @author: qyl
 */
public class RemoteInvocationHandler implements InvocationHandler {
    private Class target;

    public <T> RemoteInvocationHandler(Class<T> clazz) {
        this.target = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest ();
        request.setServiceDescriptor (ServiceDescriptor.of (target, method));
        request.setParameters (args);
        RpcResponse response = invokeRemote (request);
        if (response != null) {
            return response.getResult ();
        }
        return null;
    }

    private RpcResponse invokeRemote(RpcRequest request) {
        ConnectionManager manager = ConnectionManager.getInstance ();
        RpcClientHandler handler = manager.take ();
        RpcResponse response = handler.send (request);
        manager.put (handler, 1);
        return response;
    }
}
