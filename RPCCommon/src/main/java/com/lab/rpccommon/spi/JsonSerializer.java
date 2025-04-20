package com.lab.rpccommon.spi;


import com.alibaba.fastjson.JSON;

/**
 * @author lab
 * @Title: Serializer
 * @ProjectName RPC
 * @Description: TODO
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
