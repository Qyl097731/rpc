package com.rpc.netty.codec;

import com.rpc.netty.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @description
 * @date 2023/4/10 21:43
 * @author: qyl
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Serializer serializer;

    private Class<?> genericClass;

    public RpcDecoder(Serializer serializer, Class<?> genericClass) {
        this.serializer = serializer;
        this.genericClass = genericClass;
    }

    /**
     * Decode the from one {@link ByteBuf} to an other. This method will be called till either the input
     * {@link ByteBuf} has nothing to read when return from this method or till nothing was read from the input
     * {@link ByteBuf}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes () < 4) {
            return;
        }
        in.markReaderIndex ();
        int length = in.readInt ();
        if (in.readableBytes() < length){
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes (bytes);
        Object obj = serializer.deserialize (bytes, genericClass);
        out.add (obj);
    }
}
