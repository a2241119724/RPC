package com.lab.consumer;

import com.lab.rpc.common.message.RpcRequest;
import com.lab.rpc.common.serializer.ISerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ConsumerApplicationTests {
    @Resource
    private ISerializer serializer;

    @Test
    void contextLoads() {
        RpcRequest.RpcRequestBuilder builder = RpcRequest.builder().serverName("1").functionName("2")
                .parameters(new String[]{"1"})
                .parameterTypes(new Class[]{String.class});
        byte[] serialize = serializer.serialize(builder.build());
        System.out.println(serializer.deserialize(serialize, RpcRequest.class));
    }
}
