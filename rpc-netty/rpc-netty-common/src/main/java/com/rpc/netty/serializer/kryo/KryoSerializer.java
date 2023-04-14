package com.rpc.netty.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.rpc.netty.serializer.Serializer;
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
        Kryo kryo = KryoPool.obtain ();
        byte[] bytes = null;
        kryo.setRegistrationRequired (false);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream ();
             Output output = new Output (baos)) {
            kryo.writeObject (output, obj);
            bytes = output.toBytes ();
        } catch (IOException e) {
            log.error ("序列化失败..." + e.getMessage ());
            throw new RuntimeException (e);
        } finally {
            KryoPool.release (kryo);
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
        Kryo kryo = KryoPool.obtain ();
        kryo.setRegistrationRequired (false);
        T obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
             Input input = new Input (bis)) {
            obj = kryo.readObject (input, clazz);
        } catch (IOException e) {
            log.error ("反序列化失败..." + e.getMessage ());
            throw new RuntimeException (e);
        } finally {
            KryoPool.release (kryo);
        }
        return obj;
    }
}
