package com.rpc.netty.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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
    List<ServiceDescriptor> services;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RpcPeer)) return false;
        RpcPeer rpcPeer = (RpcPeer) o;
        return Objects.equals (host, rpcPeer.host) && Objects.equals (port, rpcPeer.port) && Objects.equals (services, rpcPeer.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash (host, port, services);
    }
}
