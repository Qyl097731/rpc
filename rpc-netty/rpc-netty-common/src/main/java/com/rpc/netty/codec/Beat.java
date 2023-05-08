package com.rpc.netty.codec;

/**
 * @description 心跳类常量
 * @date 2023/5/8 22:48
 * @author: qyl
 */
public class Beat {
    /**
     * 建议服务器端的心跳时长要比客户端的时间更长一些，因为在网络通信中，服务器可能会遇到更多的网络拥堵和延迟。
     * 如果服务器端的心跳时间设得太短，那么可能会导致频繁地发送心跳包，造成无谓的网络负担和带宽浪费。
     * 因此，为了保证网络连接的稳定性和通信效率，在实际应用中，服务器端的心跳时长通常比客户端的心跳时长要更长一些。
     */
    public static final int BEAT_INTERVAL = 30;

    public static final int BEAT_TIMEOUT = 3 * BEAT_INTERVAL;

    public static final String BEAT_ID = "PING_PONG";

    public static RpcRequest BEAT_REQUEST;

    private Beat() {}

    static {
        BEAT_REQUEST = new RpcRequest ();
        BEAT_REQUEST.setServiceId (BEAT_ID);
    }
}
