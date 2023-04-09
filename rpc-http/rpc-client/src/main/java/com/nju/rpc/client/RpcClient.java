package com.nju.rpc.client;

import com.nju.codec.Decoder;
import com.nju.codec.Encoder;
import com.nju.rpc.ReflectionUtils;

import java.lang.reflect.Proxy;

/**
 * @description 客户端
 * @date 2023/4/9 19:16
 * @author: qyl
 */
public class RpcClient {
    private RpcClientConfig config;
    private TransportSelector selector;
    private Encoder encoder;
    private Decoder decoder;

    public RpcClient() {
        this(new RpcClientConfig ());
    }

    public RpcClient(RpcClientConfig config) {
        this.config = config;
        this.selector = ReflectionUtils.newInstance (config.getSelectorClass ());
        this.encoder = ReflectionUtils.newInstance (config.getEncoderClass ());
        this.decoder = ReflectionUtils.newInstance (config.getDecoderClass ());
        selector.init (config.getServers (),config.getConnectCount (),config.getClientClass ());
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance (clazz.getClassLoader (),
                clazz.getInterfaces (),
                new RemoteInvocationHandler(clazz,encoder,decoder,selector));
    }
}
