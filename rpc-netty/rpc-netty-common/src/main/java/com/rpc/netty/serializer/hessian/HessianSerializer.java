package com.rpc.netty.serializer.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.rpc.netty.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description hession2 序列化
 * @date 2023/5/11 23:52
 * @author: qyl
 */
@Slf4j
public class HessianSerializer extends Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        byte[] bytes = null;
        // 创建字节输出流 对字节数组流进行再次封装
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            HessianOutput hessianOutput = new HessianOutput(bos);
            hessianOutput.writeObject(obj);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            log.error("序列化对象为字节数组时发生异常：", e);
        }
        return bytes;

    }

    @Override
    public <T> T deserialize(byte[] input, Class<T> clazz) {
        T t = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(input)) {
            HessianInput hessianInput = new HessianInput(bis);
            t = (T) hessianInput.readObject();
        } catch (IOException e) {
            log.error("反序列化对象时发生异常：", e);
        }
        return t;
    }
}
