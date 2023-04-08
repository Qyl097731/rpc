package com.nju.transporter;

/**
 * 1. 启动、监听端口
 * 2. 接收请求
 * 3. 关闭监听
 * @date 2023/4/8 21:37
 * @author: qyl
 */
public interface TransportServer {
    /**
     * 服务端启动
     */
    void start() throws Exception;

    /**
     * 不同端口，不同的处理器初始化
     * @param port
     * @param handler
     */
    void init(int port, RequestHandler handler);

    /**
     * 服务端关闭
     */
    void stop() throws Exception;
}
