package com.lab.rpc.common.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author lab
 * @Title: HessianSerializer
 * @ProjectName RPC
 * @Description: Hessian序列化
 * @date 2025/6/2 22:00
 */
public class HessianSerializer implements ISerializer{
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(bos);
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] obj, Class<T> clazz) {
        ByteArrayInputStream bis = new ByteArrayInputStream(obj);
        Hessian2Input in = new Hessian2Input(bis);
        try {
            return clazz.cast(in.readObject());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
