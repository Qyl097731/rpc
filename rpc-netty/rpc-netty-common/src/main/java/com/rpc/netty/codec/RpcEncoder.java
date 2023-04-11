package com.rpc.netty.codec;

import com.rpc.netty.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @description rpc编码器
 * @date 2023/4/10 21:56
 * @author: qyl
 */
public class RpcEncoder extends MessageToByteEncoder{

    private Serializer serializer;

    private Class<?> clazz;

    public RpcEncoder(Serializer serializer, Class<?> clazz) {
        this.serializer = serializer;
        this.clazz = clazz;
    }

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = serializer.serialize (msg);
        out.writeInt(bytes.length);
        out.writeBytes (bytes);
    }
}
