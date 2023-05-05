package com.netty.rpc.connect;

import com.netty.rpc.handler.NettyClientChannelInitializer;
import com.netty.rpc.config.NettyClientConfig;
import com.netty.rpc.route.LoadBalance;
import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.impl.LoadBalanceRandom;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.serializer.Serializers;
import com.rpc.netty.utils.ThreadPoolUtil;
import io.netty.bootstrap.Bootstrap;
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
import java.util.concurrent.*;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

/**
 * @description 连接池管理
 * @date 2023/4/11 17:59
 * @author: qyl
 */
@Slf4j
public class ConnectionManager {
    private final Bootstrap bootstrap = new Bootstrap ();

    private final ExecutorService threadPool = ThreadPoolUtil.getThreadPoolExecutor ();

    private ConcurrentHashMap<RpcPeer, RpcClientHandler> connectionMap = new ConcurrentHashMap<> ();

    private final CopyOnWriteArraySet<RpcPeer> serviceCache = new CopyOnWriteArraySet<> ();

    private LoadBalance loadBalance = new LoadBalanceRandom();

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
        } else if (type == CHILD_UPDATED) {
            updateServerNode (peer);
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
                    RpcClientHandler handler = future.channel ().pipeline ().get (RpcClientHandler.class);
                    connectionMap.put (server, handler);
                    log.info ("connect to server {}:{} success", server.getHost (), server.getPort ());
                } else {
                    log.error ("Can not connect to remote server, remote peer = " + server);
                }
            });
        });
    }

    /**
     * 一个服务提供者，先缓存冲删除，后添加回去。复用连接handler
     * @param peer
     */
    private void updateServerNode(RpcPeer peer) {
        serviceCache.remove (peer);
        serviceCache.add (peer);
    }

    /**
     * 移除并且关闭handler 防止资源泄露
     * @param server
     */
    private void removeAndCloseHandler(RpcPeer server) {
        RpcClientHandler handler = connectionMap.get (server);
        if (handler != null) {
            handler.close ();
        }
        serviceCache.remove (server);
        connectionMap.remove (server);
        log.info ("remove {}:{} from connection pool", server.getHost (), server.getPort ());
    }

    /**
     * 根据服务进行负载均衡，获取服务提供者
     * TODO 通过自旋等待来实现读写互斥
     *
     * @param request
     * @return
     */
    public RpcClientHandler borrow(RpcRequest request) throws Exception {
        RpcPeer peer = loadBalance.route (request.getServiceDescriptor (), connectionMap);
        RpcClientHandler handler = connectionMap.get (peer);
        if (handler == null) {
            throw new Exception ("Can not get available connection");
        } else {
            return handler;
        }
    }
}
