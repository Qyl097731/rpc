package com.netty.rpc.registry;

import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;
import com.rpc.netty.utils.JsonUtil;
import com.rpc.netty.zookeeper.CuratorClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rpc.netty.common.Constant.ZK_SERVICE_PATH;

/**
 * @description Zookeeper实现服务注册
 * @date 2023/5/2 20:05
 * @author: qyl
 */
@Slf4j
public class ZookeeperServiceRegistry extends ServiceRegistry {
    private CuratorClient client;

    private List<String> pathList = new ArrayList<> ();
    public ZookeeperServiceRegistry() {
        client = new CuratorClient ();
    }

    /**
     * 通过zookeeper进行服务注册
     * @param host
     * @param port
     * @param services
     */
    @Override
    public void register(String host, int port, Map<ServiceDescriptor, ServiceInstance> services) throws Exception {
        this.services.putAll(services);
        List<ServiceDescriptor> serviceDescriptorList = new ArrayList<> ();
        for (Map.Entry<ServiceDescriptor, ServiceInstance> entry : services.entrySet ()) {
            serviceDescriptorList.add (entry.getKey ());
        }
        RpcPeer peer = new RpcPeer (host, port, serviceDescriptorList);
        byte[] bytes = JsonUtil.serialize (peer);
        String path = ZK_SERVICE_PATH + "-" + peer.hashCode ();
        client.createPathData (path, bytes);
        pathList.add (path);
    }
}
