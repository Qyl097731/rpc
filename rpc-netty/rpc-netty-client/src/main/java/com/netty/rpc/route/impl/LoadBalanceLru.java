package com.netty.rpc.route.impl;

import com.netty.rpc.route.LoadBalance;
import com.rpc.netty.protocol.RpcPeer;
import com.rpc.netty.protocol.ServiceDescriptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description LRU 负载均衡
 * @date 2023/5/11 21:40
 * @author: qyl
 */
public class LoadBalanceLru implements LoadBalance {
    /**
     * 缓存容量
      */
    private static final int CACHE_SIZE = 3;

    private Map<ServiceDescriptor, Map<RpcPeer,RpcPeer>> cache = new ConcurrentHashMap<> ();

    private long expireTime = 0;

    @Override
    public RpcPeer doRoute(ServiceDescriptor service, List<RpcPeer> serviceProviders) {
        if (System.currentTimeMillis() > expireTime){
            cache.clear ();
            expireTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }
        /**
         * LinkedHashMap
         * a、accessOrder：ture=访问顺序排序（get/put时排序）/ACCESS-LAST；false=插入顺序排期/FIFO；
         * b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；
         *      可封装LinkedHashMap并重写该方法，比如定义最大容量，超出时返回true即可实现固定长度的LRU算法；
         */
        Map<RpcPeer, RpcPeer> lruCache = cache.getOrDefault (service,
                new LinkedHashMap<RpcPeer,RpcPeer> (16,0.75f,true){
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<RpcPeer, RpcPeer> eldest) {
                        return super.size () > CACHE_SIZE;
                    }
                });
        cache.putIfAbsent (service,lruCache);

        Set<RpcPeer> providerSet = new HashSet<> (serviceProviders);

        // add
        for (RpcPeer peer : providerSet) {
            lruCache.putIfAbsent (peer,peer);
        }

        // remove
        for (Map.Entry<RpcPeer, RpcPeer> entry : lruCache.entrySet ()) {
            RpcPeer peer = entry.getKey ();
            if (!providerSet.contains (peer)){
                lruCache.remove(peer);
            }
        }

        // choose
        RpcPeer key = lruCache.keySet ().iterator().next();
        return lruCache.get(key);
    }
}
