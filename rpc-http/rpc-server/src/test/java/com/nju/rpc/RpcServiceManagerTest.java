package com.nju.rpc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @description
 * @date 2023/4/9 17:56
 * @author: qyl
 */
class RpcServiceManagerTest {

    static RpcServiceManager manager;

    @BeforeAll
    static void init() {
        manager = new RpcServiceManager ();
    }

    @Test
    void register() {
        manager.register (ServiceTestBean.class, new ServiceTestBean ());
    }

    @Test
    void lookup() throws NoSuchMethodException {
        Request request = new Request ();
        Class<?> clazz = ServiceTestBean.class;
        manager.register (ServiceTestBean.class, new ServiceTestBean ());

        Method method = clazz.getMethod ("sayHello");
        ServiceDescriptor descriptor = ServiceDescriptor.from (clazz, method);
        request.setService (descriptor);
        RpcServiceInstance instance = manager.lookup (request);
        Assertions.assertNotNull (instance);
    }
}
