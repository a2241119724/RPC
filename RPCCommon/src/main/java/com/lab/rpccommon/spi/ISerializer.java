package com.lab.rpccommon.spi;

/**
 * @author lab
 * @Title: ISerializer
 * @ProjectName RPC
 * @Description: 序列化
 * @date 2025/4/19 16:17
 */
public interface ISerializer{
    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] obj, Class<T> clazz);
}
