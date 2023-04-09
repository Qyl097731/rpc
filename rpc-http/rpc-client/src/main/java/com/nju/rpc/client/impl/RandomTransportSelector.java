package com.nju.rpc.client.impl;

import com.nju.rpc.Peer;
import com.nju.rpc.ReflectionUtils;
import com.nju.rpc.client.TransportSelector;
import com.nju.transporter.TransportClient;
import com.sun.deploy.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @description 随机选择连接器
 * @date 2023/4/9 19:00
 * @author: qyl
 */
@Slf4j
public class RandomTransportSelector implements TransportSelector {
    private List<TransportClient> pool;

    public RandomTransportSelector() {
        this.pool = new ArrayList<> ();
    }

    @Override
    public void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz) {
        for (Peer peer : peers) {
            for (int i = 0; i < count; i++) {
                TransportClient client = ReflectionUtils.newInstance (clazz);
                client.connect (peer);
                pool.add (client);
            }
            log.info ("connect server: {}", peer);
        }
    }

    @Override
    public synchronized TransportClient select() {
        int i = new Random ().nextInt (pool.size ());
        return pool.remove (i);
    }

    @Override
    public synchronized void release(TransportClient client) {
        pool.add (client);
    }

    @Override
    public synchronized void close() {
        for (TransportClient client : pool) {
            client = null;
        }
        pool.clear ();
    }
}
