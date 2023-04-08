package com.nju.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description RPC返回
 * @date 2023/4/8 20:03
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    /**
     * 服务返回编码
     * TODO 后面将code抽取到一个静态资源类
     */
    private int code;

    /**
     * 具体的错误信息
     */
    private String message;

    /**
     * 返回的数据
     */
    private T data;

}
