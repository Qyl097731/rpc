package com.netty.rpc.handler;

import com.netty.rpc.config.NettyServerConfig;
import com.netty.rpc.handler.NettyServerHandler;
import com.rpc.netty.codec.*;
import com.rpc.netty.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.compression.Lz4FrameDecoder;
import io.netty.handler.codec.compression.Lz4FrameEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
     */
    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline ();
        // 90s 没收到客户端的请求就关闭连接
        pipeline.addLast (new IdleStateHandler (Beat.BEAT_TIMEOUT,0,0, TimeUnit.SECONDS));
        pipeline.addLast(new Lz4FrameEncoder());
        pipeline.addLast(new Lz4FrameDecoder());
        pipeline.addLast (new RpcDecoder (ReflectionUtils.create (config.getSerializerClass ()),RpcRequest.class));
        pipeline.addLast (new LengthFieldBasedFrameDecoder (65535,0,4,0,0));
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
