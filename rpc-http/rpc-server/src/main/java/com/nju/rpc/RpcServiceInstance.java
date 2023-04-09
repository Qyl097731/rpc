package com.nju.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @description 服务实例
 * @date 2023/4/9 17:22
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcServiceInstance {
    /**
     * 目标类
     */
    private Object target;

    /**
     * 暴露的方法
     */
    private Method method;
}
