package com.rpc.netty.utils;

import com.rpc.netty.annotation.RpcService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * @description 注解扫描工具类
 * @date 2023/4/15 19:05
 * @author: qyl
 */
public class AnnotationUtils {
    /**
     * 扫描当前上下文的所有被RpcService标注的类，过滤出本地的文件
     * @param packageNames
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getServicesClasses(String[] packageNames) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<> ();
        ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        for (String packageName : packageNames) {
            String path = packageName.replace (".", "/");
            Enumeration<URL> resources = classLoader.getResources (path);
            while (resources.hasMoreElements ()) {
                URL url = resources.nextElement ();
                if (url.getProtocol ().equals ("file")) {
                    File file = new File (URLDecoder.decode (url.getFile (), "utf-8"));
                    if (file.isDirectory ()) {
                        File[] files = file.listFiles ();
                        for (File f : files) {
                            String fileName = f.getName ();
                            if (fileName.endsWith (".class")) {
                                String className = packageName + "." + fileName.substring (0, fileName.lastIndexOf ("."));
                                Class<?> clazz = Class.forName (className);
                                if (clazz.isAnnotationPresent (RpcService.class)
                                        || getExplodedMethods (clazz).length > 0){
                                    classes.add (clazz);
                                }
                            }else {
                                classes.addAll (getServicesClasses (new String[] {packageName + "."+fileName}));
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * 获取所有Rpc provider暴露的方法
     * @param clazz
     * @return 所有Rpc provider暴露的方法
     * @param <T>
     */
    public static <T> Method[] getExplodedMethods(Class<T> clazz) {
        Method[] methods = clazz.getMethods ();
        if (clazz.isAnnotationPresent(RpcService.class)) {
            return methods;
        } else {
            if (methods.length > 0) {
                return Arrays.stream (methods)
                        .filter (method -> method.isAnnotationPresent (RpcService.class))
                        .toArray (Method[]::new);
            }else {
                return new Method[0];
            }
        }
    }
}
