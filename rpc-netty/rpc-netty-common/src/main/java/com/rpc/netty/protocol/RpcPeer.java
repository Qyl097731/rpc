package com.rpc.netty.protocol;

import lombok.Data;

/**
 * @author nsec
 */
@Data
public class RpcPeer {
    private String host;
    private Integer port;
}
