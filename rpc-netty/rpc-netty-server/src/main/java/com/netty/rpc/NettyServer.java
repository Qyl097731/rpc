package com.netty.rpc;

import ch.qos.logback.core.spi.LifeCycle;
import com.netty.rpc.manager.ServiceManager;
import com.rpc.netty.codec.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @date 2023/4/11 1:38
 * @author: qyl
 */
@Slf4j
public class NettyServer implements LifeCycle {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup ();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup ();
    private Channel channel;

    private ServerBootstrap server;

    private static final String host = "127.0.0.1";
    private static final int port = 3000;

    public NettyServer() {
        if (Objects.isNull (ServiceManager.getInstance ())){
            log.error("注册中心初始化失败");
            throw new RuntimeException("注册中心初始化失败");
        }else {
            log.info("注册中心初始化成功");
        }
    }

    @Override
    public void start() {
        try {
            server = new ServerBootstrap ();
            server.group (bossGroup, workerGroup)
                    .channel (NioServerSocketChannel.class)
                    .childHandler (new NettyServerInitializer ());
            ChannelFuture future = server.bind (host,port).sync ();
            channel = future.channel ();
        } catch (InterruptedException e) {
            log.error ("Netty服务启动失败", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (channel != null) {
                channel.close ().sync ();
                bossGroup.shutdownGracefully ().sync ();
                workerGroup.shutdownGracefully ().sync ();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    public <T> void register(Class<T> clazz, T instance) {
        ServiceManager.register (clazz, instance);
    }
}
