package com.rpc.netty.serializer.protocol;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.rpc.netty.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java序列化Protocol Buffer框架—ProtoStuff
 *
 * @author nsec
 */
@Slf4j
public class ProtostuffSerializer extends Serializer {
    private Map<Class<?>, Schema<?>> cacheSchema = new ConcurrentHashMap<>();
    private Objenesis objenesis = new ObjenesisStd(true);

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException("protostuff 序列化失败");
        }finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] input, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T message = (T)objenesis.newInstance(clazz);
        try {
            ProtostuffIOUtil.mergeFrom(input, message, schema);
            return message;
        }catch (Exception e) {
            throw new RuntimeException("protostuff 反序列化失败",e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> clazz) {
        return (Schema<T>)cacheSchema.computeIfAbsent(clazz, RuntimeSchema::createFrom);
    }
}
