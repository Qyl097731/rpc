package com.netty.rpc.route.impl;

import com.netty.rpc.route.LoadBalance;
import com.rpc.netty.protocol.RpcPeer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description LFU 负载均衡使用
 * @date 2023/5/11 20:45
 * @author: qyl
 */
public class LoadBalanceLfu implements LoadBalance {
    private ConcurrentMap<String, HashMap<RpcPeer, Integer>> cache = new ConcurrentHashMap<> (10);
    private long expireTime = 0;
    private final int MAX_TIME = 10000;

    @Override
    public RpcPeer doRoute(String serviceName, List<RpcPeer> serviceProviders) {
        if (CollectionUtils.isEmpty (serviceProviders)) {
            throw new RuntimeException ("No available service");
        }

        if (System.currentTimeMillis () > expireTime) {
            cache.clear ();
            expireTime = System.currentTimeMillis () + 24 * 60 * 60 * 1000;
        }

        // 服务节点缓存信息更新
        HashMap<RpcPeer, Integer> usedFrequencyMap = cache.get (serviceName);
        if (usedFrequencyMap == null) {
            usedFrequencyMap = new HashMap<> ();
            cache.putIfAbsent (serviceName, usedFrequencyMap);
        }
        for (RpcPeer provider : serviceProviders) {
            if (!usedFrequencyMap.containsKey (provider) || usedFrequencyMap.get (provider) > MAX_TIME) {
                usedFrequencyMap.put (provider, 0);
            }
        }

        // 服务节点缓存-删除废物节点
        Set<RpcPeer> serviceProviderSet = new HashSet<> (serviceProviders);
        for (Map.Entry<RpcPeer, Integer> entry : usedFrequencyMap.entrySet ()) {
            if (!serviceProviderSet.contains (entry.getKey ())) {
                usedFrequencyMap.remove (entry.getKey ());
            }
        }

        // 挑选最近最少的服务节点
        RpcPeer chosen = null;
        for (RpcPeer rpcPeer : serviceProviders) {
            if (usedFrequencyMap.containsKey (rpcPeer) && usedFrequencyMap.get (rpcPeer) < usedFrequencyMap.get (chosen)) {
                chosen = rpcPeer;
            }
        }
        usedFrequencyMap.computeIfPresent (chosen, (key, value) -> value + 1);
        return chosen;
    }
}
