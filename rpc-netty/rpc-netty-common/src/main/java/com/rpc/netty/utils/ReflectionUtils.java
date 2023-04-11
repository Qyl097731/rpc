package com.rpc.netty.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description 反射工具类
 * @date 2023/4/11 1:03
 * @author: qyl
 */
@Slf4j
public class ReflectionUtils {
    /**
     * 获取共有方法，后面进行限制
     *
     * @param <T>
     * @return public 方法
     */
    public static <T> Method[] getPublicMethods(Class<T> clazz) {
        return clazz.getMethods ();
    }

    /**
     * 反射创建对象
     * @param clazz
     * @return 实列对象
     * @param <T>
     */
    public static <T> T create(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error ("create instance error " + e.getMessage ());
            throw new RuntimeException (e);
        }
    }

    /**
     * 调用方法
     * @param target
     * @param method
     * @param args
     * @return 返回代理结果
     */
    public static Object invoke(Object target,Method method,Object... args){
        Object obj;
        try {
            obj = method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error " + e.getMessage ());
            throw new RuntimeException (e);
        }
        return obj;
    }
}