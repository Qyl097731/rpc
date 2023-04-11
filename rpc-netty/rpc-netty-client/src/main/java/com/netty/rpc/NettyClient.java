package com.netty.rpc;


import com.netty.rpc.handler.RemoteInvocationHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Proxy;

/**
 * @description 客户端服务器
 * @date 2023/4/11 16:23
 * @author: qyl
 */
public class NettyClient {
    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private static String remoteAddress = "127.0.0.1";
    private static int port = 3000;

    public NettyClient(){
        this(remoteAddress,port);
    }

    public NettyClient(String remoteAddress,int port){
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientChannelInitializer());

        try {
            ChannelFuture future = bootstrap.connect (remoteAddress, port).sync ();
            channel = future.channel ();
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
    }

    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance (
                clazz.getClassLoader (),
                new Class[] {clazz},
                new RemoteInvocationHandler (clazz)
        );
    }

    private void stop(){
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
    }
}
