package com.nju.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 服务描述
 * @date 2023/4/8 19:52
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDescriptor {
    /**
     * 服务类型
     */
    private String clazz;

    /**
     * 服务方法
     */
    private String method;

    /**
     * 服务返回类型
     */
    private String returnType;

    /**
     * 入参类型
     */
    private String[] parameterTypes;
}
