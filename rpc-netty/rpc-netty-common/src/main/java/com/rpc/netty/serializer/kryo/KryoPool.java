package com.rpc.netty.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * 使用对象池的 Kryo 序列化工具类，基于对象池的单例模式实现。
 * 序列化和反序列化时通过对象池重复利用 Kryo 实例和 ByteArrayOutputStream 实例，避免频繁创建和销毁对象带来的性能问题。
 *
 * @date 2023/4/14 16:33
 * @author: qyl
 */
public class KryoPool {

    private static final int MAX_SIZE = 16;
    private static final KryoFactory FACTORY = () -> {
        Kryo kryo = new Kryo();
        kryo.setReferences (false);
        kryo.setRegistrationRequired(false);
        /**
         * 默认情况下，Kryo 使用的是 Kryo.DefaultInstantiatorStrategy，该策略对象会尝试使用类的无参构造函数进行对象的实例化，如果该类没有无参构造函数，则会抛出异常。
         * StdInstantiatorStrategy 是一个标准的实例化策略对象，会尝试使用类的其他构造函数进行对象的实例化，比如有参构造函数或者工厂方法等。
         */
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return kryo;
    };
    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo> (true, false, MAX_SIZE) {
        @Override
        protected Kryo create() {
            return FACTORY.create ();
        }
    };

    public static Kryo obtain(){
        return KRYO_POOL.obtain();
    }

    public static void release(Kryo kryo){
        KRYO_POOL.free(kryo);
    }
}
