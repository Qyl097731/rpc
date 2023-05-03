package com.netty.rpc;


import com.netty.rpc.connect.ConnectionManager;
import com.netty.rpc.handler.RemoteInvocationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @description 客户端服务器
 * @date 2023/4/11 16:23
 * @author: qyl
 */
@Slf4j
public class NettyClient {
    public NettyClient() {
        ConnectionManager connectionManager = ConnectionManager.getInstance ();
        if (Objects.isNull (connectionManager)){
            log.error("连接管理器获取失败失败");
            throw new RuntimeException("连接管理器初始化失败");
        }else {
            connectionManager.updateConnectionPool();
            log.info("连接管理器初始化成功");
        }
    }

    public <T> T getProxy(Class<T> clazz,String version) {
        return (T) Proxy.newProxyInstance (clazz.getClassLoader (),
                new Class[]{clazz},
                new RemoteInvocationHandler (clazz,version));
    }
}
