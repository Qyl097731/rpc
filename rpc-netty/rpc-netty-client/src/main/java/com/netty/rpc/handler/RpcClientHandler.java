package com.netty.rpc.handler;

import com.netty.rpc.connect.ConnectionManager;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.protocol.RpcPeer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;


/**
 * @description 代理类进行远程嗲用
 * @date 2023/4/11 17:34
 * @author: qyl
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private Channel channel;
    private RpcResponse response;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        log.info ("收到服务端响应:{}", response);
        this.response = response;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered (ctx);
        this.channel = ctx.channel ();
    }

    public RpcResponse send(RpcRequest request){
        try {
            channel.writeAndFlush (request).sync ();
            return response;
        } catch (InterruptedException e) {
            log.error("发送请求异常", e);
            throw new RuntimeException (e);
        }
    }
}
