package com.lab.rpc.common.spi;


import com.alibaba.fastjson.JSON;

/**
 * @author lab
 * @title Serializer
 * @projectName RPC
 * @description Json序列化
 * @date 2025/4/19 16:16
 */
public class JsonSerializer implements ISerializer{
    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] obj, Class<T> clazz) {
        return JSON.parseObject(new String(obj), clazz);
    }
}
