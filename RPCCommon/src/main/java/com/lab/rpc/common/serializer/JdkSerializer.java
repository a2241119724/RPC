package com.lab.rpc.common.serializer;

import java.io.*;

/**
 * @author lab
 * @title JdkSerializer
 * @projectName RPC
 * @description Java自带序列化
 * @date 2025/4/28 15:45
 */
public class JdkSerializer implements ISerializer{
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)){
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] obj, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(obj);
        try (ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream)) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
