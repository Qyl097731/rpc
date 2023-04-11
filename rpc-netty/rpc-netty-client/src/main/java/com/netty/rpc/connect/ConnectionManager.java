package com.netty.rpc.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description 连接池管理
 * @date 2023/4/11 17:59
 * @author: qyl
 */
public class ConnectionManager {
    private ConnectionManager(){}

    private static class SingletonHolder{
        private static final ConnectionManager instance = new ConnectionManager ();
    }
}
