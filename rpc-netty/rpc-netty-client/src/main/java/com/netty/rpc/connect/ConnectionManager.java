package com.netty.rpc.connect;

import com.netty.rpc.handler.NettyClientChannelInitializer;
import com.netty.rpc.config.NettyClientConfig;
import com.netty.rpc.route.LoadBalance;
import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.impl.RandomLoadBalance;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.serializer.Serializers;
import com.rpc.netty.utils.ThreadPoolUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

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
    private final Bootstrap bootstrap = new Bootstrap ();

    private final CopyOnWriteArrayList<RpcClientHandler> connectionPool = new CopyOnWriteArrayList<> ();

    private final ExecutorService threadPool = ThreadPoolUtil.getThreadPoolExecutor ();

    private ConcurrentHashMap<RpcPeer, RpcClientHandler> connectionMap = new ConcurrentHashMap<> ();

    private final CopyOnWriteArraySet<RpcPeer> serviceCache = new CopyOnWriteArraySet<> ();

    private LoadBalance loadBalance;

    private static class SingletonHolder {
        private static final ConnectionManager instance = new ConnectionManager ();
    }

    private ConnectionManager() {
        NettyClientConfig config = new NettyClientConfig ();
        config.setSerializerClass (Serializers.KRYO);
        bootstrap.group (new NioEventLoopGroup ())
                .channel (NioSocketChannel.class)
                .option (ChannelOption.TCP_NODELAY, true)
                .option (ChannelOption.SO_KEEPALIVE, true)
                .option (ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler (new NettyClientChannelInitializer (config));
    }

    public static ConnectionManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 本地连接，没有引入ZK，同时未进行动态上下线的监听
     */
    public void updateConnectionPool() {
        updateConnectionPool (DEFAULT_SIZE);
    }

    /**
     * 只能连接一个服务提供者，没有上下文监听
     *
     * @param size 默认连接池大小
     */
    public void updateConnectionPool(int size) {
        int capacity = size > MAX_SIZE ? DEFAULT_SIZE : size;
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

    /**
     * 1.进行zk注册之后，连接池的更新，通过差集进行连接池更新；
     * 2.实现多个服务提供者的连接
     *
     * @param serviceList
     */
    public void updateConnectionPool(List<RpcPeer> serviceList) {
        if (!CollectionUtils.isEmpty (serviceList)) {
            HashSet<RpcPeer> currentServiceSet = new HashSet<> (serviceList);
            for (RpcPeer server : currentServiceSet) {
                if (!serviceCache.contains (server)) {
                    connectServerNode (server);
                }
            }

            for (RpcPeer server : serviceCache) {
                if (!currentServiceSet.contains (server)) {
                    removeAndCloseHandler (server);
                }
            }
        } else {
            log.info ("no service provider to connect");
            serviceCache.clear ();
        }
    }

    /**
     * 根据事件类型进行不同的更新
     * @param type
     * @param peer
     */
    public void updateConnectionPool(PathChildrenCacheEvent.Type type, RpcPeer peer) {
        if (type == CHILD_ADDED) {
            connectServerNode (peer);
        } else if (type == CHILD_REMOVED) {
            removeAndCloseHandler (peer);
        }else if (type == CHILD_UPDATED){
            removeAndCloseHandler (peer);
            connectServerNode(peer);
        }
    }


    /**
     * 建立连接，线程池加速连接建立
     *
     * @param server
     */
    private void connectServerNode(RpcPeer server) {
        List<ServiceDescriptor> services = server.getServices ();
        if (CollectionUtils.isEmpty (services)) {
            log.info ("no services on server {}:{}", server.getHost (), server.getPort ());
            return;
        }
        serviceCache.add (server);
        for (ServiceDescriptor service : services) {
            log.info ("New service info, name: {}, version: {}", service.getMethod (),
                    service.getVersion ());
        }

        threadPool.submit (() -> {
            InetSocketAddress address = new InetSocketAddress (server.getHost (), server.getPort ());
            bootstrap.connect (address).addListener ((ChannelFutureListener) future -> {
                if (future.isSuccess ()) {
                    log.info ("connect to server {}:{} success", server.getHost (), server.getPort ());
                    RpcClientHandler handler = future.channel ().pipeline ().get (RpcClientHandler.class);
                    connectionMap.put (server, handler);
                } else {
                    log.error ("Can not connect to remote server, remote peer = " + server);
                }
            });
        });
    }

    /**
     * 移除并且关闭handler 防止资源泄露
     * @param server
     */
    private void removeAndCloseHandler(RpcPeer server) {
        RpcClientHandler handler = connectionMap.get (server);
        if (handler != null){
            handler.close();
        }
        serviceCache.remove (server);
        connectionMap.remove (server);
        log.info ("remove {}:{} from connection pool", server.getHost (), server.getPort ());
    }

    public RpcClientHandler borrow() {
        return loadBalance.choose (connectionPool);
    }

    public void release(RpcClientHandler handler) {
        connectionPool.add (handler);
    }
}
