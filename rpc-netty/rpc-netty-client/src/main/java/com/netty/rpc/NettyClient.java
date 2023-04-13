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

/**
 * @description 客户端服务器
 * @date 2023/4/11 16:23
 * @author: qyl
 */
@Slf4j
public class NettyClient {
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private static String remoteAddress = "127.0.0.1";
    private static int port = 3000;

    private static int POOLSIZE = 10;

    public NettyClient() {
        this (remoteAddress, port);
    }

    public NettyClient(String remoteAddress, int port) {
        bootstrap = new Bootstrap ();
        group = new NioEventLoopGroup ();
        bootstrap.group (group)
                .channel (NioSocketChannel.class)
                .handler (new NettyClientChannelInitializer ());

        ChannelFuture future = null;
        try {
            future = bootstrap.connect (remoteAddress, port).sync ();
            RpcClientHandler handler = future.channel ().pipeline ().get (RpcClientHandler.class);
            ConnectionManager.getInstance ().put (handler, 1);
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance (clazz.getClassLoader (),
                new Class[]{clazz},
                new RemoteInvocationHandler (clazz));
    }

    public void stop() {
        group.shutdownGracefully ();
    }
}
