package com.netty.rpc.handler;

import com.rpc.netty.codec.Beat;
import com.rpc.netty.codec.RpcFuture;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 代理类进行远程嗲用
 * @date 2023/4/11 17:34
 * @author: qyl
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private Channel channel;
    private ConcurrentHashMap<String, RpcFuture> futureMap = new ConcurrentHashMap<> ();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        log.info ("收到服务端响应:{}", response);
        RpcFuture future = futureMap.get (response.getServiceId ());
        if (future != null) {
            future.done (response);
            futureMap.remove (response.getServiceId ());
        } else {
            log.error ("响应过时....");
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered (ctx);
        this.channel = ctx.channel ();
    }

    /**
     * 发送请求进行远程调用，注意这里必须先进性添加后进行发送，否则后续很容易还没记录，请求已经返回，导致死锁
     *
     * @param request
     * @return
     */
    public RpcFuture send(RpcRequest request) {
        RpcFuture future = new RpcFuture ();
        future.setRequest (request);
        futureMap.put (request.getServiceId (), future);
        try {
            channel.writeAndFlush (request).sync ();
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
        return future;
    }

    /**
     * 一旦空缓冲区发送完毕关闭channel，就关闭channel，防止资源泄漏和进一步的数据传输。
     */
    public void close() {
        channel.writeAndFlush (Unpooled.EMPTY_BUFFER).addListener (ChannelFutureListener.CLOSE);
    }

    /**
     * 处理空闲事件，太久没发送过请求就发送一个心跳，防止被误关闭
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state ();
            if (state == IdleState.WRITER_IDLE) {
                send (Beat.BEAT_REQUEST);
                log.info("write idle happen: [{}]", ctx.channel().toString());
            }
        } else {
            super.userEventTriggered (ctx, evt);
        }
    }
}
