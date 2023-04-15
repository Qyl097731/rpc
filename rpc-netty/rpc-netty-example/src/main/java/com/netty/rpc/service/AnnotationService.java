package com.netty.rpc.service;

/**
 * @description 测试注解注入，版本号等
 * @date 2023/4/15 17:41
 * @author: qyl
 */
public interface AnnotationService {
    /**
     * 返回版本号
     *
     * @return 版本号
     */
    Integer getVersion();
}
