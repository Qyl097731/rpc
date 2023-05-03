package com.netty.rpc.discovery;

import com.netty.rpc.connect.ConnectionManager;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.utils.JsonUtil;
import com.rpc.netty.zookeeper.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.ArrayList;
import java.util.List;

import static com.rpc.netty.common.Constant.ZK_SERVICE_PATH;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

/**
 * @description 服务发现 同时更新连接数
 * @date 2023/5/3 19:15
 * @author: qyl
 */
@Slf4j
public class ServiceDiscovery {
    private CuratorClient curatorClient;

    public ServiceDiscovery() {
        curatorClient = new CuratorClient ();
        discoveryService ();
    }

    public void discoveryService() {
        log.info ("service info init.....");
        try {
            getServicesAndUpdateConnectionPool ();
            PathChildrenCacheListener listener = (client, event) -> {
                PathChildrenCacheEvent.Type type = event.getType ();
                ChildData data = event.getData ();
                switch (type) {
                    case CONNECTION_RECONNECTED:
                        log.info ("Connection re-connected: " + data.getPath ());
                        getServicesAndUpdateConnectionPool ();
                        break;
                    case CHILD_ADDED:
                        log.info ("Child node added: " + data.getPath ());
                        getServicesAndUpdateConnectionPool (CHILD_ADDED, data);
                        break;
                    case CHILD_REMOVED:
                        log.info ("Child node removed: " + data.getPath ());
                        getServicesAndUpdateConnectionPool (CHILD_REMOVED, data);
                        break;
                    case CHILD_UPDATED:
                        log.info ("Child node updated: " + data.getPath ());
                        getServicesAndUpdateConnectionPool (CHILD_UPDATED, data);
                        break;
                }
            };
            curatorClient.reWatchChildNods (ZK_SERVICE_PATH, listener);
        } catch (Exception e) {
            log.error ("service discovery error:{}", e.getMessage ());
        }
    }

    private void getServicesAndUpdateConnectionPool() throws Exception {
        List<String> nodeList = curatorClient.getChildren (ZK_SERVICE_PATH);
        List<RpcPeer> peers = new ArrayList<> ();
        for (String node : nodeList) {
            String path = ZK_SERVICE_PATH + "/" + node;
            byte[] data = curatorClient.getData (path);
            RpcPeer peer = JsonUtil.deserialize (data, RpcPeer.class);
            peers.add (peer);
        }
        log.info ("service discovered {}", peers);
        updateConnectionPool (peers);
    }

    private void getServicesAndUpdateConnectionPool(PathChildrenCacheEvent.Type type, ChildData childData) {
        byte[] bytes = childData.getData ();
        RpcPeer peer = JsonUtil.deserialize (bytes, RpcPeer.class);
        log.info("Child data updated, path:{},type:{},data:{},", childData.getPath (), type, peer);
        updateConnectionPool (type, peer);
    }

    private void updateConnectionPool(List<RpcPeer> peers) {
        ConnectionManager.getInstance ().updateConnectionPool (peers);
    }

    private void updateConnectionPool(PathChildrenCacheEvent.Type type, RpcPeer peer) {
        ConnectionManager.getInstance ().updateConnectionPool (type,peer);
    }
}
