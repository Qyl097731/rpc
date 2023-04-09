package com.nju.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @description 服务描述
 * @date 2023/4/8 19:52
 * @author: qyl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDescriptor {
    /**
     * 服务实现类名
     */
    private String clazz;

    /**
     * 服务方法
     */
    private String method;

    /**
     * 服务返回类型
     */
    private String returnType;

    /**
     * 入参类型
     */
    private String[] parameterTypes;

    public static <T> ServiceDescriptor from(Class<T> object, Method method) {
        ServiceDescriptor descriptor = new ServiceDescriptor ();
        descriptor.setClazz (object.getName ());
        descriptor.setMethod (method.getName ());
        descriptor.setReturnType (method.getReturnType ().getTypeName ());
        String[] parameterTypes = Arrays.stream (method.getParameterTypes ())
                .map (Class::getTypeName)
                .toArray (String[]::new );
        descriptor.setParameterTypes (parameterTypes);
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;
        ServiceDescriptor that = (ServiceDescriptor) o;
        return Objects.equals (clazz, that.clazz) && Objects.equals (method, that.method) && Objects.equals (returnType, that.returnType) && Arrays.equals (parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash (clazz, method, returnType);
        result = 31 * result + Arrays.hashCode (parameterTypes);
        return result;
    }
}
