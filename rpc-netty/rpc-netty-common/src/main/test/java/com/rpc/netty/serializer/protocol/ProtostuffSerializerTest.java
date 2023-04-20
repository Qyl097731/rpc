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

import static org.junit.jupiter.api.Assertions.*;

class ProtostuffSerializerTest {

    private static Serializer serializer;

    @BeforeAll
    static void setUp() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        serializer = new ProtostuffSerializer<User>();
    }

    @AfterAll
    static void tearDown() {
        serializer = null;
    }
    @Test
    void serialize() {
        User user = new User(10, "test");
        byte[] bytes = serializer.serialize(user);
        assertNotNull(bytes);
    }

    @Test
    void deserialize() {
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