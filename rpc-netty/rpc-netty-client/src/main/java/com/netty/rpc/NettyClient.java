package com.netty.rpc;


import com.netty.rpc.connect.ConnectionManager;
import com.netty.rpc.handler.RemoteInvocationHandler;
import com.netty.rpc.handler.RpcClientHandler;
import com.rpc.netty.protocol.RpcPeer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
        if (Objects.isNull (ConnectionManager.getInstance ())){
            log.error("连接管理器初始化失败");
            throw new RuntimeException("连接管理器初始化失败");
        }else {
            log.info("连接管理器初始化成功");
        }
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance (clazz.getClassLoader (),
                new Class[]{clazz},
                new RemoteInvocationHandler (clazz));
    }
}
