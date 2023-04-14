package com.rpc.netty.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * @description kryo工厂
 * @date 2023/4/14 16:42
 * @author: qyl
 */
@FunctionalInterface
public interface KryoFactory {
    /**
     * 创建实列kryo
     * @return Kryo实例
     */
    Kryo create();
}
