package com.nju.codec.impl;

import com.alibaba.fastjson.JSON;
import com.nju.codec.Encoder;

/**
 * @description 基于json的序列化实现
 * @date 2023/4/8 20:56
 * @author: qyl
 */
public class JSONEncoder implements Encoder {
    @Override
    public <T> byte[] encode(T obj) {
        return JSON.toJSONBytes (obj);
    }
}
