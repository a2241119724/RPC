package com.lab.rpc.client.netty;

import com.lab.rpc.client.annotation.RPCReference;
import com.lab.rpc.client.netty.handler.NettyClientHandler;
import com.lab.rpc.client.discovery.IServerDiscovery;
import com.lab.rpc.common.enumerate.ProtocolMessageTypeEnum;
import com.lab.rpc.common.message.ProtocolMessage;
import com.lab.rpc.common.message.RpcRequest;
import com.lab.rpc.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author lab
 * @title ServerCallAop
 * @projectName RPC
 * @description 代理缓存，代理相关的函数
 * @date 2025/4/13 14:50
 */
@Slf4j
public class ClientAop implements MethodInterceptor, BeanPostProcessor, CommandLineRunner {
    @Resource
    private NettyClient nettyClient;
    @Resource
    private IServerDiscovery serverDiscovery;

    private final Map<String, Object> proxy = new HashMap<>();
    private final Set<String> servers = new HashSet<>();

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        NettyClientHandler nettyClientHandler = nettyClient.getConnection(method.getDeclaringClass().getSimpleName());
        if(nettyClientHandler==null){
            return null;
        }
        RpcRequest request = RpcRequest.builder()
                .functionName(method.getName())
                .serverName(method.getDeclaringClass().getSimpleName())
                .parameterTypes(method.getParameterTypes())
                .parameters(objects).build();
        ProtocolMessage.Header header = ProtocolMessage.Header.builder()
                .type(ProtocolMessageTypeEnum.REQUEST.getKey()).build();
        //
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        protocolMessage.setHeader(header);
        protocolMessage.setBody(request);
        RpcResponse response = nettyClientHandler.send(protocolMessage);
        Object result = response.getResult();
        if(result==null){
            log.info(response.getError());
        }
        return result;
    }

    private Object getProxy(Class<?> serverClass, ProxyType proxyType){
        if(!proxy.containsKey(serverClass.getName())){
            Object serverProxy;
            switch (proxyType) {
                case JDK:
                    serverProxy = Proxy.newProxyInstance(serverClass.getClassLoader(), new Class[]{serverClass},
                        (Object proxy, Method method, Object[] args) -> intercept(proxy, method, args, null));
                    break;
                case Cglib:
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(serverClass);
                    enhancer.setCallback(this);
                    serverProxy = enhancer.create();
                    break;
                default:
                    throw new RuntimeException("不支持的代理类型");
            }
            proxy.put(serverClass.getName(),serverProxy);
        }
        return proxy.get(serverClass.getName());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean,@NonNull String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if(field.getAnnotation(RPCReference.class)!=null){
                servers.add(field.getType().getSimpleName());
                Object proxy = getProxy(field.getType(), ProxyType.Cglib);
                try {
                    field.setAccessible(true);
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    @Override
    public void run(String... args){
        serverDiscovery.connect();
        for (String server : servers) {
            nettyClient.preCreateConnection(server);
        }
    }

    public enum ProxyType{
        // jdk代理
        JDK,
        // cglib代理
        Cglib
    }
}
