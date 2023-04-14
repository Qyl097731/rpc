package com.netty.rpc.connect;

import com.netty.rpc.NettyClientChannelInitializer;
import com.netty.rpc.route.LoadBalance;
import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.impl.RandomLoadBalance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description 连接池管理
 * @date 2023/4/11 17:59
 * @author: qyl
 */
@Slf4j
public class ConnectionManager {
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 20;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 3000;
    private Bootstrap bootstrap;
    private CopyOnWriteArrayList<RpcClientHandler> connectionPool;
    private LoadBalance loadBalance;

    private static class SingletonHolder {
        private static final ConnectionManager instance = new ConnectionManager ();
    }

    private ConnectionManager() {
        this (DEFAULT_SIZE);
    }

    private ConnectionManager(int size) {
        bootstrap = new Bootstrap ();
        bootstrap.group (new NioEventLoopGroup ())
                .channel (NioSocketChannel.class)
                .option (ChannelOption.TCP_NODELAY, true)
                .option (ChannelOption.SO_KEEPALIVE, true)
                .option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler (new NettyClientChannelInitializer ());

        int capacity = size > MAX_SIZE ? DEFAULT_SIZE : size;

        connectionPool = new CopyOnWriteArrayList<>();
        for (int i = 0; i < capacity; i++) {
            try {
                Channel channel = bootstrap.connect (DEFAULT_HOST, DEFAULT_PORT).sync ().channel ();
                RpcClientHandler handler = channel.pipeline ().get (RpcClientHandler.class);
                connectionPool.add (handler);
            } catch (InterruptedException e) {
                log.error ("fail to connect to server");
                throw new RuntimeException (e);
            }
        }
        loadBalance = new RandomLoadBalance ();
    }
    public static ConnectionManager getInstance() {
        return SingletonHolder.instance;
    }

    public RpcClientHandler borrow(){
        return loadBalance.choose (connectionPool);
    }
    public void release(RpcClientHandler handler){
        connectionPool.add (handler);
    }
}
