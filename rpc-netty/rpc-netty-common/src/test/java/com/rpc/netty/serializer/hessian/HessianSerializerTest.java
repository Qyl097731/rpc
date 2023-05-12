package com.rpc.netty.serializer.hessian;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * @description Hessian2 测试
 * @date 2023/5/12 20:40
 * @author: qyl
 */
class HessianSerializerTest {
    @Data
    private static class User implements Serializable {
        private String username;
        private int age;
        private List<Goods> goods = new ArrayList<> ();
    }

    @Data
    @AllArgsConstructor
    private static class Goods implements Serializable{
        private String gid;
        private BigDecimal cost;
    }

    @Test
    void serialize() {
        // 序列化
        List<Goods> goods = new ArrayList<Goods>(){{
            add(new Goods (UUID.randomUUID().toString(),BigDecimal.ONE));
            add (new Goods(UUID.randomUUID().toString(),BigDecimal.TEN));
        }};
        User user = new User ();
        user.setUsername ("张三");
        user.setAge (11);
        user.setGoods (goods);
        HessianSerializer hessianSerializer = new HessianSerializer ();
        byte[] serialize = hessianSerializer.serialize (user);

        Assertions.assertNotNull (serialize);
    }

    @Test
    void deserialize() {
        // 序列化
        List<Goods> goods = new ArrayList<Goods>(){{
            add(new Goods (UUID.randomUUID().toString(),BigDecimal.ONE));
            add (new Goods(UUID.randomUUID().toString(),BigDecimal.TEN));
        }};
        User user = new User ();
        user.setUsername ("张三");
        user.setAge (11);
        user.setGoods (goods);
        HessianSerializer hessianSerializer = new HessianSerializer ();
        byte[] serialize = hessianSerializer.serialize (user);

        // 反序列化
        hessianSerializer.deserialize (serialize,User.class);
        Assertions.assertEquals("张三", user.getUsername());
        Assertions.assertEquals(11, user.getAge());
        Assertions.assertEquals(2, user.getGoods().size());
    }
}
