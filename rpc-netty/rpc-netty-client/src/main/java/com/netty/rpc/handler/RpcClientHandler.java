package com.netty.rpc.handler;

import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 代理类进行远程嗲用
 * @date 2023/4/11 17:34
 * @author: qyl
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        log.info("收到服务端响应:{}", msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered (ctx);
        this.channel = ctx.channel();
    }

    public void send(RpcRequest request){
        try {
            ChannelFuture future = channel.writeAndFlush (request).sync ();
        } catch (InterruptedException e) {
            log.error("发送请求异常", e);
            throw new RuntimeException (e);
        }
    }
}
