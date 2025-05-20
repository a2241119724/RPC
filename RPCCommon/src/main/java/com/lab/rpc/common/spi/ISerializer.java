package com.lab.rpc.common.spi;

/**
 * @author lab
 * @title ISerializer
 * @projectName RPC
 * @description 序列化
 * @date 2025/4/19 16:17
 */
public interface ISerializer{
    /**
     * 序列化
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     * @param <T> 对象类型
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param obj 需要反序列化的字节数组
     * @param clazz 对象字节码
     * @param <T> 对象类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] obj, Class<T> clazz);
}
