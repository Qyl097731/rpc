package com.rpc.netty.serializer;

/**
 * @description 序列化抽象类
 * @date 2023/4/10 21:05
 * @author: qyl
 */
public abstract class Serializer {
    /**
     * 序列化
     *
     * @param obj
     * @return byte数组
     */
    public abstract <T> byte[] serialize(T obj);

    /**
     * 反序列化
     *
     * @param input
     * @param clazz
     * @return clazz类型的对象
     */
    public abstract <T> T deserialize(byte[] input, Class<T> clazz);
}
