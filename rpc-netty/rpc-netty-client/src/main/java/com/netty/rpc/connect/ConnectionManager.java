package com.netty.rpc.connect;

import com.netty.rpc.cas.AtomicIntegerCas;
import com.rpc.netty.protocol.RpcPeer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description 连接池管理
 * @date 2023/4/11 17:59
 * @author: qyl
 */
public class ConnectionManager {
    private static final ConcurrentHashMap<RpcPeer, Channel> connectionMap = new ConcurrentHashMap<>();

    private ConnectionManager() {
    }

    private static class SingletonHolder {
        private static final ConnectionManager instance = new ConnectionManager();
    }

    public void createPool(RpcPeer peer, Channel channel) {
        connectionMap.put(peer, channel);
    }

}
