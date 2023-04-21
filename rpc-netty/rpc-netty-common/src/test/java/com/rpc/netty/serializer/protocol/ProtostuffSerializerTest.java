package com.rpc.netty.serializer.protocol;

import com.rpc.netty.serializer.Serializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProtostuffSerializerTest {

    private static Serializer serializer;

    @BeforeAll
    static void setUp() {
    }

    @AfterAll
    static void tearDown() {

    }
    @Test
    void serializeForObjectUseProtoStuff() {
        serializer = new ProtostuffSerializer();
        User user = new User(10, "test");
        byte[] bytes = serializer.serialize(user);
        assertNotNull(bytes);
    }
    @Test
    void deserializeForObjectUseProtoStuff() {
        serializer = new ProtostuffSerializer();
        User user = new User(10, "test");
        byte[] bytes = serializer.serialize(user);
        User deUser = serializer.deserialize(bytes,User.class);
        assertTrue(deUser.equals(user));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class User{
    private int age;
    private String name;
}
