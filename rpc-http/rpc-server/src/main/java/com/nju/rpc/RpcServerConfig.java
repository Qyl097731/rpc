package com.nju.rpc;

import com.nju.codec.Decoder;
import com.nju.codec.Encoder;
import com.nju.codec.impl.JSONDecoder;
import com.nju.codec.impl.JSONEncoder;
import com.nju.transporter.TransportServer;
import com.nju.transporter.impl.HttpTransportServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description server配置类
 * @date 2023/4/9 17:06
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcServerConfig {
    private Class<? extends TransportServer> transportClass = HttpTransportServer.class;

    private Class<? extends Decoder> decoderClass = JSONDecoder.class;

    private Class<? extends Encoder> encoderClass = JSONEncoder.class;

    private int port = 3000;
}
