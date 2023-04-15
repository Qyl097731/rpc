package com.rpc.netty.utils;

import com.rpc.netty.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @description 反射工具类
 * @date 2023/4/11 1:03
 * @author: qyl
 */
@Slf4j
public class ReflectionUtils {
    /**
     * 反射创建对象
     *
     * @param clazz
     * @param <T>
     * @return 实列对象
     */
    public static <T> T create(Class<T> clazz) {
        try {
            return clazz.newInstance ();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error ("create instance error " + e.getMessage ());
            throw new RuntimeException (e);
        }
    }

    /**
     * 调用方法
     *
     * @param target
     * @param method
     * @param args
     * @return 返回代理结果
     */
    public static Object invoke(Object target, Method method, Object... args) {
        Object obj;
        try {
            obj = method.invoke (target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error ("invoke error " + e.getMessage ());
            throw new RuntimeException (e);
        }
        return obj;
    }
}
