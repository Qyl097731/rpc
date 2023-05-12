package com.rpc.netty.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @description 响应类
 * @date 2023/4/11 0:05
 * @author: qyl
 */
@Data
public class RpcResponse implements Serializable {
    private String serviceId;
    private Integer code;
    private Object result;
    private String msg;

    public boolean isSuccess() {
        return code == 200;
    }
}
