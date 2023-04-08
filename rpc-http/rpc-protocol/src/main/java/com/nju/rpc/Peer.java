package com.nju.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description 端口
 * @date 2023/4/8 19:50
 * @author: qyl
 */
@Data
@AllArgsConstructor
public class Peer {
    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private int port;
}
