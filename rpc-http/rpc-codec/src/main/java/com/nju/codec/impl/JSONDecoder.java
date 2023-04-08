package com.nju.codec.impl;

import com.alibaba.fastjson.JSON;
import com.nju.codec.Decoder;

/**
 * @description 基于Json的反序列化
 * @date 2023/4/8 20:59
 * @author: qyl
 */
public class JSONDecoder implements Decoder {
    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject (bytes,clazz);
    }
}
