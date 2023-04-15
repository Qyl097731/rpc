package com.netty.rpc.service.impl;

import com.netty.rpc.service.AnnotationService;
import com.rpc.netty.annotation.RpcService;

/**
 * @description 测试注解以及版本号
 * @date 2023/4/15 17:42
 * @author: qyl
 */
@RpcService(version = "1.0")
public class AnnotationServiceImpl1 implements AnnotationService {
    @Override
    public Integer getVersion() {
        return 1;
    }
}
