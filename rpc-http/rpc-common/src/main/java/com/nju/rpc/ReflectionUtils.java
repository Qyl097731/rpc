package com.nju.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @description 反射工具类
 * @date 2023/4/8 20:08
 * @author: qyl
 */
public class ReflectionUtils {
    /**
     * 根据class创建对象
     * @param clazz 待创建对象的类
     * @return 对象实例
     * @param <T>
     */
    public static <T> T newInstance(Class<T> clazz){
        try {
            return clazz.newInstance ();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * 获取class的public方法
     * @param clazz
     * @return 当前类的公开方法
     * @param <T>
     */
    public static<T> Method[] getPublicMethods(Class<T> clazz){
        return Arrays.stream (clazz.getDeclaredMethods ())
                .filter (m-> Modifier.isPublic (m.getModifiers ()))
                .toArray (Method[]::new);
    }

    /**
     * 调用指定对象的指定方法
     * @param obj 被调用对象
     * @param method 对调用的方法
     * @param args 方法的参数
     * @return 返回结果
     */
    public static Object invoke(Object obj,Method method, Object... args){
        try {
            return method.invoke (obj,args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException (e);
        }
    }
}
