package com.netty.rpc.service.impl;

import com.netty.rpc.service.AnnotationService;
import com.rpc.netty.annotation.RpcService;

/**
 * @description 测试注解 版本号
 * @date 2023/4/15 17:44
 * @author: qyl
 */
@RpcService(version = "2.0")
public class AnnotationServiceImpl2 implements AnnotationService {
    @Override
    public Integer getVersion() {
        return 2;
    }
}
