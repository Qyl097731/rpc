package com.nju.codec;

/**
 * @description 序列化
 * @date 2023/4/8 20:51
 * @author: qyl
 */
public interface Encoder {
    /**
     * 序列化接口
     * @param obj
     * @return 字节数组
     */
    <T> byte[] encode(T obj);
}
