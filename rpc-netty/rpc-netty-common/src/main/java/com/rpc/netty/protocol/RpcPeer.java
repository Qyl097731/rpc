package com.rpc.netty.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author nsec
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcPeer {
    /**
     * 域名地址
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 当前节点能提供的所有服务
     */
//    List<ServiceDescriptor> services;
}
