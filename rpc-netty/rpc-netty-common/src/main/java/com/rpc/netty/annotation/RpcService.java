package com.rpc.netty.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ppc注解，后续通过扫描所有该注解进行服务注册
 * @author asus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RpcService {
    Class<?> interfaceClass() default void.class;

    String interfaceName() default "";

    String version() default "";
}
