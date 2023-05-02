package com.netty.rpc.registry;

import com.rpc.netty.codec.RpcRequest;

/**
 * @description
 * @date 2023/5/2 20:05
 * @author: qyl
 */
public class ZookeeperServiceRegistry extends ServiceRegistry{
    /**
     * @param clazz
     * @param instance
     * @param <T>
     * @param <P>
     */
    @Override
    public <T, P> void register(Class<T> clazz, P instance) {

    }

    /**
     * 根据请求 进行服务查找
     *
     * @param request
     * @param <T>
     * @return
     */
    public <T> T lookup(RpcRequest request) {
        return null;
    }
}
