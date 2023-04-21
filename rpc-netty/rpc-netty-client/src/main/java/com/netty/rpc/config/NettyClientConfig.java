package com.netty.rpc.config;

import com.rpc.netty.serializer.Serializer;
import lombok.Data;

/**
 * @description
 * @date 2023/4/21 19:21
 * @author: qyl
 */
@Data
public class NettyClientConfig {
    public Class<? extends Serializer> serializerClass;
    private int port = 3000;
    private String host = "127.0.0.1";
}
