package com.rpc.netty.serializer;


import com.rpc.netty.serializer.hessian.HessianSerializer;
import com.rpc.netty.serializer.kryo.KryoSerializer;
import com.rpc.netty.serializer.protocol.ProtostuffSerializer;

/**
 * @description 可插拔方式的序列化方式
 * @date 2023/4/10 21:05
 * @author: qyl
 */
public class Serializers {
    public static final Class<? extends Serializer> KRYO = KryoSerializer.class;
    public static final Class<? extends Serializer> PROTOSTUFF = ProtostuffSerializer.class;
    public static final Class<? extends Serializer> HESSIAN2 = HessianSerializer.class;
}
