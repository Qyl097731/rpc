package com.netty.rpc.handler;

import com.netty.rpc.invoker.ServiceInvoker;
import com.netty.rpc.manager.ServiceManager;
import com.netty.rpc.registry.ServiceInstance;
import com.rpc.netty.codec.Beat;
import com.rpc.netty.codec.RpcRequest;
import com.rpc.netty.codec.RpcResponse;
import com.rpc.netty.utils.ThreadPoolUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @description 服务处理器 将客户端的request进行解码 编码传递
 * @date 2023/4/11 13:30
 * @author: qyl
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final ExecutorService threadPool = ThreadPoolUtil.getThreadPoolExecutor ();

    /**
     * 通过线程池实现业务和IO隔离
     * Is called for each message of type {@link RpcRequest}.
     *
     * @param ctx     the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                belongs to
     * @param request the message to handle
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        log.info ("收到客户端的请求 ..." + request.toString ());
        if(Beat.BEAT_ID.equals (request.getServiceId ())){
            log.info("服务器收到心跳包 ...");
        }else {
            threadPool.execute (new RespondTask (ctx, request));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info (ctx.channel ().remoteAddress () + " 连接成功...");
        super.channelActive (ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error (ctx.channel ().remoteAddress () + " 异常..." + cause.getMessage ());
        super.exceptionCaught (ctx, cause);
    }

    private class RespondTask implements Runnable {
        private final ChannelHandlerContext ctx;
        private final RpcRequest request;

        public RespondTask(ChannelHandlerContext ctx, RpcRequest request) {
            this.ctx = ctx;
            this.request = request;
        }

        /**
         * 分离业务 和 IO
         */
        @Override
        public void run() {
            ServiceInstance service = ServiceManager.lookup (request);
            RpcResponse response = new RpcResponse ();
            try {
                Object r = ServiceInvoker.invoke (service, request);
                response.setResult (r);
                response.setServiceId (request.getServiceId ());
                response.setCode (HttpResponseStatus.OK.code ());
            } catch (Exception e) {
                response.setResult (e);
                response.setCode (HttpResponseStatus.INTERNAL_SERVER_ERROR.code ());
                log.error ("服务调用失败 ..." + e.getMessage ());
            }
            ctx.channel ().writeAndFlush (response);
        }
    }

    /**
     * 处理空闲状态，也就是长时间没收到读请求就关闭
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果长时间未收到心跳包，就说明客户端断线，就断开连接
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state ();
            if (state == IdleState.READER_IDLE){
                log.info ("长时间未收到心跳包，断开连接");
                ctx.channel ().close ();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
