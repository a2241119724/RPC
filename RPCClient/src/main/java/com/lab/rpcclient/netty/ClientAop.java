package com.lab.rpcclient.netty;

import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpcclient.zookeeper.ServerDiscovery;
import com.lab.rpccommon.pojo.RPCRequest;
import com.lab.rpccommon.pojo.RPCResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author lab
 * @Title: ServerCallAop
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/13 14:50
 */
@Slf4j
public class ClientAop implements MethodInterceptor {
    @Resource
    private NettyClient nettyClient;

    @Resource
    private ServerDiscovery serverDiscovery;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        InetSocketAddress instance = serverDiscovery.getInstanceByRandom(method.getDeclaringClass().getSimpleName());
        if(instance == null){
            return null;
        }
        NettyClientHandler nettyClientHandler = nettyClient.getConnection(instance);
        RPCRequest request = RPCRequest.builder().requestId(UUID.randomUUID().toString())
                .functionName(method.getName())
                .serverName(method.getDeclaringClass().getSimpleName())
                .parameterTypes(method.getParameterTypes())
                .parameters(objects).build();
        RPCResponse response = nettyClientHandler.send(request);
        Object result = response.getResult();
        if(result==null){
            log.info(response.getError());
        }
        return result;
    }
}
