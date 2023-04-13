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

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @description 代理类进行远程嗲用
 * @date 2023/4/11 17:34
 * @author: qyl
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private Channel channel;
    final Lock lock = new ReentrantLock ();
    private RpcResponse response;
    private Semaphore semaphore = new Semaphore (0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        log.info ("收到服务端响应:{}", response);
        this.response = response;
        semaphore.release();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered (ctx);
        this.channel = ctx.channel ();
    }

    public RpcResponse send(RpcRequest request) {
        lock.lock ();
        try {
            channel.writeAndFlush (request);
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        } finally {
            lock.unlock ();
        }
        return response;

    }
}
