package com.lab.consumer.netty;

import com.alibaba.fastjson.JSON;
import com.lab.common.pojo.RPCRequest;
import com.lab.common.pojo.RPCResponse;
import com.lab.consumer.netty.handler.NettyClientHandler;
import com.lab.consumer.zookeeper.ServerDiscovery;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lab
 * @Title: ServerCallAop
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/13 14:50
 */
@Slf4j
@Controller
public class ServerCallAop implements MethodInterceptor {
    @Resource
    private NettyClient nettyClient;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        InetSocketAddress instance = ServerDiscovery.getInstance().getInstanceByRandom(method.getDeclaringClass().getSimpleName());
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
