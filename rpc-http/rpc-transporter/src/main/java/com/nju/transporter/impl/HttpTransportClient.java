package com.nju.transporter.impl;

import com.nju.rpc.Peer;
import com.nju.transporter.TransportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @description 基于HTTP实现客户端传输器
 * 通过Http实现长连接，模拟RPC
 * @date 2023/4/8 21:43
 * @author: qyl
 */
@Slf4j
public class HttpTransportClient implements TransportClient {
    private URL url;

    private HttpURLConnection conn;

    @Override
    public void connect(Peer peer){
        try {
            /**
             * 连接设置参数
             */
            this.url = new URL ("http",peer.getHost (),peer.getPort (),null);
            this.conn = (HttpURLConnection)url.openConnection ();
            this.conn.setDoInput (true);
            this.conn.setDoInput (true);
            this.conn.setUseCaches (false);
            this.conn.setRequestMethod ("POST");
            // 小小的优化，设置超时时间(以毫秒为单位)，否则可能网路很慢，严重影响后续任务推进
            this.conn.setConnectTimeout (3000);
            this.conn.setReadTimeout (3000);
            this.conn.connect ();
        } catch (IOException e) {
            log.info ("连接失败....");
            if (this.conn != null){
                close ();
            }
            throw new RuntimeException (e);
        }
    }

    @Override
    public InputStream write(InputStream data) {
        try {
            /**
             * 先复制数据流到conn的输出流，getResponseCode\getInputStream\getErrorStream的时候才会把数据真正发送出去
             */
            IOUtils.copy (data,conn.getOutputStream ());
            int code = conn.getResponseCode ();
            if (code == HttpURLConnection.HTTP_OK){
                return conn.getInputStream ();
            }else {
                return conn.getErrorStream ();
            }
        } catch (IOException e) {
            log.info ("信息发送失败....");
            if (this.conn != null){
                close ();
            }
            throw new RuntimeException (e);
        }
    }

    @Override
    public void close() {
        this.url = null;
        this.conn.disconnect ();
        this.conn = null;
        log.info ("连接关闭........");
    }
}
