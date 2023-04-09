package com.nju.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description RPC请求
 * @date 2023/4/8 19:54
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    /**
     * 请求的服务
     */
    private ServiceDescriptor service;

    /**
     * 参数
     * TODO 后期看看能不能把Object进行优化
     */
    private Object[] parameters;
}
