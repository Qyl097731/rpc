package com.rpc.netty.codec;

import lombok.Data;

/**
 * @description 响应类
 * @date 2023/4/11 0:05
 * @author: qyl
 */
@Data
public class RpcResponse {
    private Integer code;

    private String msg;
}
