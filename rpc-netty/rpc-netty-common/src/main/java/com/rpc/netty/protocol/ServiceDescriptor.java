package com.rpc.netty.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @description 服务描述
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDescriptor implements Serializable {
    /**
     * 服务实现类名
     */
    private String clazz;

    /**
     * 服务方法
     */
    private String method;

    /**
     * 版本号
     */
    private String version;

    /**
     * 服务返回类型
     */
    private String returnType;

    /**
     * 入参类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 根据类 和 指定的方法 放回服务描述
     *
     * @param <T>
     * @param clazz
     * @param method
     * @param version
     * @return 描述
     */
    public static <T> ServiceDescriptor of(Class<T> clazz, Method method, String version) {
        ServiceDescriptor descriptor = new ServiceDescriptor ();
        descriptor.setClazz (clazz.getName ());
        descriptor.setMethod (method.getName ());
        descriptor.setParameterTypes (method.getParameterTypes ());
        descriptor.setReturnType (method.getReturnType ().getTypeName ());
        descriptor.setVersion (version);
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceDescriptor)) return false;
        ServiceDescriptor that = (ServiceDescriptor) o;
        return Objects.equals (clazz, that.clazz) && Objects.equals (method, that.method)
                && Objects.equals (returnType, that.returnType) && Objects.equals (version, that.version)
                && Arrays.equals (parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        int reuslt = Objects.hash (clazz, method, returnType, version);
        reuslt = 31 * reuslt + Arrays.hashCode (parameterTypes);
        return reuslt;
    }
}
