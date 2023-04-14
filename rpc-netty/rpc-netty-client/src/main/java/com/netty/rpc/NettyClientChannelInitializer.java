package com.netty.rpc;

import com.netty.rpc.handler.RpcClientHandler;
import com.rpc.netty.codec.RpcDecoder;
import com.rpc.netty.codec.RpcEncoder;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.serializer.kryo.KryoSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 客户端初始化
 * @date 2023/4/11 16:30
 * @author: qyl
 */
@Slf4j
public class NettyClientChannelInitializer extends ChannelInitializer<Channel> {
    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     * @throws Exception is thrown if an error occurs. In that case it will be handled by
     *                   {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
     *                   the {@link Channel}.
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline ();
        pipeline.addLast (new RpcEncoder (new KryoSerializer (), RpcRequest.class));
        pipeline.addLast (new RpcDecoder (new KryoSerializer (), RpcResponse.class));
        pipeline.addLast(new RpcClientHandler());
    }

    /**
     * Handle the {@link Throwable} by logging and closing the {@link Channel}. Sub-classes may override this.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientHandler exceptionCaught", cause);
        super.exceptionCaught (ctx, cause);
    }
}
