package com.lab.rpc.common.serializer;

import com.alibaba.fastjson2.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lab.rpc.common.message.RpcRequest;
import com.lab.rpc.common.message.RpcResponse;
import com.lab.rpc.common.message.proto.RPCRequestProto;
import com.lab.rpc.common.message.proto.RPCResponseProto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @title ProtoSerializer
 * @projectName RPC
 * @description Proto序列化
 * @date 2025/4/28 19:27
 */
public class ProtoSerializer implements ISerializer{
    /** 缓存Class */
    private static final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

    @Override
    public <T> byte[] serialize(T obj) {
        if(obj instanceof RpcRequest){
            RpcRequest rpcRequest = (RpcRequest) obj;
            RPCRequestProto.RPCRequest.Builder builder = RPCRequestProto.RPCRequest.newBuilder().setFunctionName(rpcRequest.getFunctionName())
                    .setServerName(rpcRequest.getServerName());
            int count = rpcRequest.getParameterTypes().length;
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            Object[] parameters = rpcRequest.getParameters();
            for(int i=0; i< count; i++){
                builder.addParameterTypes(parameterTypes[i].getName());
                builder.addParameters(String.valueOf(parameters[i]));
            }
            return builder.build().toByteArray();
        }else if(obj instanceof RpcResponse){
            RpcResponse rpcResponse = (RpcResponse) obj;
            RPCResponseProto.RPCResponse.Builder builder = RPCResponseProto.RPCResponse.newBuilder();
            if(rpcResponse.getResult()!=null){
                builder.setResult(JSON.toJSONString(rpcResponse.getResult()));
            }
            if(rpcResponse.getError()!=null){
                builder.setError(rpcResponse.getError());
            }
            return builder.build().toByteArray();
        }
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] obj, Class<T> clazz) {
        if(clazz == RpcRequest.class){
            try {
                RPCRequestProto.RPCRequest proto = RPCRequestProto.RPCRequest.parseFrom(obj);
                RpcRequest.RpcRequestBuilder builder = RpcRequest.builder().serverName(proto.getServerName())
                        .functionName(proto.getFunctionName());
                int count = proto.getParametersCount();
                Object[] parameters = new Object[count];
                Class<?>[] parameterTypes = new Class[count];
                for(int i=0; i<count; i++){
                    parameters[i] = proto.getParameters(i);
                    parameterTypes[i] = CLASS_CACHE.computeIfAbsent(proto.getParameterTypes(i), k -> {
                        try {
                            return Class.forName(k);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                builder.parameters(parameters);
                builder.parameterTypes(parameterTypes);
                return clazz.cast(builder.build());
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }else if(clazz == RpcResponse.class){
            RpcResponse.RpcResponseBuilder builder = RpcResponse.builder();
            RPCResponseProto.RPCResponse proto;
            try {
                proto = RPCResponseProto.RPCResponse.parseFrom(obj);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
            return clazz.cast(builder.result(JSON.parse(proto.getResult()))
                    .error(proto.getError()).build());
        }
        return null;
    }
}
