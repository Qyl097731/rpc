package com.rpc.netty.annotation;

import java.lang.annotation.*;

/**
 * Ppc注解，后续通过扫描所有该注解进行服务注册
 * @author asus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RpcService {
    String version() default "";
}
