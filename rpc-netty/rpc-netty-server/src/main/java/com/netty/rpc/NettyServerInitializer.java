package com.netty.rpc;

import com.netty.rpc.config.NettyServerConfig;
import com.netty.rpc.handler.NettyServerHandler;
import com.rpc.netty.codec.RpcDecoder;
import com.rpc.netty.codec.RpcEncoder;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.serializer.Serializer;
import com.rpc.netty.serializer.kryo.KryoSerializer;
import com.rpc.netty.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 进行服务器初始化，将需要的处理器添加进pipeline
 * @date 2023/4/11 13:32
 * @author: qyl
 */
@Slf4j
public class NettyServerInitializer extends ChannelInitializer<Channel> {
    private final NettyServerConfig config;
    public NettyServerInitializer(NettyServerConfig config) {
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
        pipeline.addLast (new RpcDecoder (ReflectionUtils.create (config.getSerializerClass ()),RpcRequest.class));
        pipeline.addLast (new RpcEncoder (ReflectionUtils.create (config.getSerializerClass ()),RpcResponse.class));
        pipeline.addLast (new NettyServerHandler ());
    }

    /**
     * Handle the {@link Throwable} by logging and closing the {@link Channel}.
     * Subclasses may override this.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyServerInitializer exceptionCaught", cause);
        super.exceptionCaught (ctx, cause);
    }
}
