package com.nju.codec;

/**
 * @description 反序列化
 * @date 2023/4/8 20:53
 * @author: qyl
 */
public interface Decoder {
    /**
     * 反序列化接口
     * @param bytes
     * @param clazz
     * @return 返回反序列化对象
     * @param <T>
     */
    <T> T decode(byte[] bytes,Class<T> clazz);
}
