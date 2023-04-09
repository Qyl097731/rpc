package com.nju.transporter;

import com.nju.rpc.Peer;

import java.io.InputStream;

/**
 * @description 客户端传输
 * 1. 创建连接
 * 2. 发送数据，并且等待响应
 * 3. 关闭连接
 * @date 2023/4/8 21:32
 * @author: qyl
 */
public interface TransportClient {
    /**
     * 连接
     */
    void connect(Peer peer);

    /**
     * 发送数据
     * @param data
     * @return
     */
    InputStream write(InputStream data);

    /**
     * 关闭连接
     */
    void close();
}
