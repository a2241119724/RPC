package com.lab.rpccommon.spi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lab.rpccommon.message.RPCHeartRequest;
import com.lab.rpccommon.message.RPCHeartResponse;
import com.lab.rpccommon.message.RPCRequest;
import com.lab.rpccommon.message.RPCResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author lab
 * @Title: JavaSerializer
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/28 15:45
 */
public class KryoSerializer implements ISerializer {
    private final ThreadLocal<Kryo> kryo = ThreadLocal.withInitial(() -> {
        Kryo k = new Kryo();
        k.register(RPCRequest.class);
        k.register(RPCResponse.class);
        k.register(RPCHeartRequest.class);
        k.register(RPCHeartResponse.class);
        return k;
    });

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.get().writeClassAndObject(output, obj);
        output.close();
        return baos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input input = new Input(bais);
        return clazz.cast(kryo.get().readClassAndObject(input));
    }
}