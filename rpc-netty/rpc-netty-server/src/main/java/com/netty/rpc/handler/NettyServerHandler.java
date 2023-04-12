package com.netty.rpc.handler;

import com.netty.rpc.invoker.ServiceInvoker;
import com.netty.rpc.manager.ServiceInstance;
import com.netty.rpc.manager.ServiceManager;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 服务处理器 将客户端的request进行解码 编码传递
 * @date 2023/4/11 13:30
 * @author: qyl
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * Is called for each message of type {@link RpcRequest}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        ServiceInstance service = ServiceManager.lookup (msg);
        RpcResponse response = new RpcResponse ();
        try {
            Object r = ServiceInvoker.invoke (service, msg);
            response.setResult (r);
            response.setCode (HttpResponseStatus.OK.code ());
        }catch (Exception e) {
            response.setResult(e);
            response.setCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            log.error("服务调用失败 ..." + e.getMessage ());
        }
        ctx.channel ().writeAndFlush (response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info (ctx.channel ().remoteAddress () + " 连接成功...");
        super.channelActive (ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(ctx.channel().remoteAddress() + " 异常..."+cause.getMessage());
        super.exceptionCaught (ctx, cause);
    }
}
