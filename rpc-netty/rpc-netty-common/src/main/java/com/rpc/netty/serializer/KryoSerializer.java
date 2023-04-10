package com.rpc.netty.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description kryo序列化
 * @date 2023/4/10 21:16
 * @author: qyl
 */
@Slf4j
public class KryoSerializer extends Serializer {
    /**
     * 序列化
     *
     * @param obj
     * @return byte数组
     */
    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = new Kryo ();
        kryo.register (obj.getClass ());
        byte[] bytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream ();
             Output output = new Output (baos)) {
            kryo.writeObject (output, obj);
            bytes = output.toBytes ();
        } catch (IOException e) {
            log.error ("序列化失败..." + e.getMessage ());
            throw new RuntimeException (e);
        }
        return bytes;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @return clazz类型的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = new Kryo ();
        kryo.register (clazz);
        T obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
             Input input = new Input (bis)) {
            obj = kryo.readObjectOrNull (input, clazz);
        } catch (IOException e) {
            log.error ("反序列化失败..." + e.getMessage ());
            throw new RuntimeException (e);
        }
        return obj;
    }
}
