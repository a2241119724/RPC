package com.lab.rpc.common.serializer;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;

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
        return JSON.parseObject(new String(obj), clazz, JSONReader.Feature.SupportClassForName);
    }
}
