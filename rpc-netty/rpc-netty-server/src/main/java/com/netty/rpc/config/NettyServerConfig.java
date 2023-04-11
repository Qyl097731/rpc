package com.netty.rpc.config;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Data;

/**
 * @description 服务配置类
 * @date 2023/4/11 14:23
 * @author: qyl
 */
@Data
public class NettyServerConfig {
    private Class<? extends ByteToMessageDecoder> encoderClass;
    private Class<? extends MessageToByteEncoder<ByteBuf>> decoderClass;
    private int port = 3000;
}
