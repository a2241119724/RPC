package com.lab.rpc.common.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lab.rpc.common.message.RpcHeartRequest;
import com.lab.rpc.common.message.RpcHeartResponse;
import com.lab.rpc.common.message.RpcRequest;
import com.lab.rpc.common.message.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author lab
 * @title JdkSerializer
 * @projectName RPC
 * @description Kryo序列化
 * @date 2025/4/28 15:45
 */
public class KryoSerializer implements ISerializer {
    private final ThreadLocal<Kryo> kryo = ThreadLocal.withInitial(() -> {
        Kryo k = new Kryo();
        k.register(RpcRequest.class);
        k.register(RpcResponse.class);
        k.register(RpcHeartRequest.class);
        k.register(RpcHeartResponse.class);
        k.setRegistrationRequired(false);
        return k;
    });

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream);
        kryo.get().writeClassAndObject(output, obj);
        output.close();
        return stream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        Input input = new Input(stream);
        Object obj = kryo.get().readClassAndObject(input);
        input.close();
        return clazz.cast(obj);
    }
}