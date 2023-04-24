package com.netty.rpc;

import com.netty.rpc.config.NettyClientConfig;
import com.netty.rpc.handler.RpcClientHandler;
import com.rpc.netty.codec.RpcDecoder;
import com.rpc.netty.codec.RpcEncoder;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.Lz4FrameDecoder;
import io.netty.handler.codec.compression.Lz4FrameEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 客户端初始化
 * @date 2023/4/11 16:30
 * @author: qyl
 */
@Slf4j
public class NettyClientChannelInitializer extends ChannelInitializer<Channel> {
    private final NettyClientConfig config;
    public NettyClientChannelInitializer(NettyClientConfig config) {
        this.config = config;
    }

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
        pipeline.addLast(new Lz4FrameEncoder());
        pipeline.addLast(new Lz4FrameDecoder());
        pipeline.addLast (new RpcEncoder (ReflectionUtils.create (config.getSerializerClass ()),RpcRequest.class));
        pipeline.addLast (new RpcDecoder (ReflectionUtils.create (config.getSerializerClass ()),RpcResponse.class));
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
