package com.nju.rpc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.AbstractSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description ReflectionUtils测试类
 * @date 2023/4/8 20:34
 * @author: qyl
 */
public class ReflectionUtilsTest {

    @Test
    void newInstance() {
        TestClass testClass = ReflectionUtils.newInstance (TestClass.class);
        Assertions.assertNotNull (testClass);
    }

    @Test
    void getPublicMethods() {
        Method[] methods = ReflectionUtils.getPublicMethods (TestClass.class);
        Assertions.assertEquals (1,methods.length);
        Assertions.assertEquals ("b",methods[0].getName ());
    }

    @Test
    void invoke() {
        Method[] methods = ReflectionUtils.getPublicMethods (TestClass.class);
        Method method = methods[0];
        TestClass t = new TestClass ();
        Object r = ReflectionUtils.invoke (t, method);
        Assertions.assertEquals ("b",r);
    }
}
