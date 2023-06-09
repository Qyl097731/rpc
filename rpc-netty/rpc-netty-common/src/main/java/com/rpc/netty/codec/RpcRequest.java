package com.rpc.netty.codec;

import com.rpc.netty.protocol.ServiceDescriptor;
import lombok.Data;

import java.io.Serializable;

/**
 * @description rpc传输的消息
 * @date 2023/4/10 21:53
 * @author: qyl
 */
@Data
public class RpcRequest implements Serializable {
    private String serviceId;
    private ServiceDescriptor serviceDescriptor;
    private Object[] parameters;
}
