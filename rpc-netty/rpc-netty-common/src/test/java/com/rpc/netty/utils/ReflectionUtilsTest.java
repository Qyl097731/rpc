package com.rpc.netty.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @description ReflectionUtils测试
 * @date 2023/4/22 0:04
 * @author: qyl
 */
class ReflectionUtilsTest {

    /**
     * objenesis是否会进行初始化
     */
    @Test
    void testNotInitWhenCreatedByObjenesis(){
        Objenesis objenesis = new ObjenesisStd (true);
        TestBean bean = objenesis.newInstance (TestBean.class);
        Assertions.assertNull (bean.getMap ());
    }

    /**
     * reflect 是否会进行初始化
     */
    @Test
    void testNeedInitWhenCreatedByReflect(){
        Assertions.assertThrows (Exception.class,()->ReflectionUtils.create (Test.class));
    }

    @Test
    void invoke() throws NoSuchMethodException {
        TestBean bean = ReflectionUtils.create (TestBean.class);
        Assertions.assertEquals (0,ReflectionUtils.invoke(bean, TestBean.class.getDeclaredMethod("getAge")));
    }

    private class TestBean{
        private Map<String,String> map = new ConcurrentHashMap<> ();
        private int age;
        public TestBean(int age){
            this.age = age;
        }

        public int getAge(){
            return age;
        }

        public Map<String, String> getMap() {
            return map;
        }
    }
}
