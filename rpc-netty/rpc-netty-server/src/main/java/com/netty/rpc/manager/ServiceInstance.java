package com.netty.rpc.manager;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @description rpc服务实例
 * @date 2023/4/11 14:41
 * @author: qyl
 */
@Data
@AllArgsConstructor
public class ServiceInstance {
    /**
     * 具体哪个实列
     */
    private Object target;
    /**
     * 具体哪个服务
     */
    private Method method;
}
