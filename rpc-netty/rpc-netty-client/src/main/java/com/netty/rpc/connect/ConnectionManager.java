package com.netty.rpc.connect;

import com.netty.rpc.route.LoadBalance;
import com.netty.rpc.handler.RpcClientHandler;
import com.netty.rpc.route.impl.RandomLoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description 连接池管理
 * @date 2023/4/11 17:59
 * @author: qyl
 */
@Slf4j
public class ConnectionManager {
    private ConnectionManager(){}
    private BlockingQueue<RpcClientHandler> connectionPool = new LinkedBlockingQueue<RpcClientHandler> (5);

    private LoadBalance loadBalance = new RandomLoadBalance ();

    private static class SingletonHolder{
        private static final ConnectionManager instance = new ConnectionManager ();
    }

    public static ConnectionManager getInstance(){
        return SingletonHolder.instance;
    }

    public synchronized void put(RpcClientHandler handler, int poolSize){
        try {
            for (int i = 0; i < poolSize; i++) {
                connectionPool.put(handler);
            }
        } catch (InterruptedException e) {
            log.error("put error", e);
            throw new RuntimeException (e);
        }
    }

    public synchronized RpcClientHandler take(){
        return loadBalance.take (connectionPool);
    }
}
