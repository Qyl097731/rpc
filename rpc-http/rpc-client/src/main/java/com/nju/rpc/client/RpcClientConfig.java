package com.nju.rpc.client;

import com.nju.codec.Decoder;
import com.nju.codec.Encoder;
import com.nju.codec.impl.JSONDecoder;
import com.nju.codec.impl.JSONEncoder;
import com.nju.rpc.Peer;
import com.nju.rpc.client.impl.RandomTransportSelector;
import com.nju.transporter.TransportClient;
import com.nju.transporter.impl.HttpTransportClient;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @description 客户端配置
 * @date 2023/4/9 19:04
 * @author: qyl
 */
@Data
public class RpcClientConfig {
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;
    private Class<? extends TransportSelector> selectorClass = RandomTransportSelector.class;
    private Class<? extends TransportClient> clientClass = HttpTransportClient.class;
    private int connectCount = 1;
    private List<Peer> servers = Arrays.asList (
            new Peer ("127.0.0.1",3000)
    );
}
