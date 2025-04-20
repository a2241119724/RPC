package com.lab.rpcclient.netty;

import com.lab.rpcclient.annotation.RPCResource;
import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpcclient.zookeeper.ServerDiscovery;
import com.lab.rpccommon.enum_.ProtocolMessageTypeEnum;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCRequest;
import com.lab.rpccommon.pojo.RPCResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lab
 * @Title: ServerCallAop
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/13 14:50
 */
@Slf4j
public class ClientAop implements MethodInterceptor, BeanPostProcessor {
    @Resource
    private NettyClient nettyClient;
    @Resource
    private ServerDiscovery serverDiscovery;

    private Map<String, Object> proxy = new HashMap<>();

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        InetSocketAddress instance = serverDiscovery.getInstance(method.getDeclaringClass().getSimpleName());
        if(instance == null){
            return null;
        }
        NettyClientHandler nettyClientHandler = nettyClient.getConnection(instance);
        RPCRequest request = RPCRequest.builder()
                .functionName(method.getName())
                .serverName(method.getDeclaringClass().getSimpleName())
                .parameterTypes(method.getParameterTypes())
                .parameters(objects).build();
        ProtocolMessage.Header header = ProtocolMessage.Header.builder()
                .type(ProtocolMessageTypeEnum.REQUEST.getKey()).build();
        //
        ProtocolMessage<RPCRequest> protocolMessage = new ProtocolMessage<>();
        protocolMessage.setHeader(header);
        protocolMessage.setBody(request);
        RPCResponse response = nettyClientHandler.send(protocolMessage);
        Object result = response.getResult();
        if(result==null){
            log.info(response.getError());
        }
        return result;
    }

    private Object getProxy(Class serverClass, ProxyType proxyType){
        if(!proxy.containsKey(serverClass.getName())){
            Object serverProxy = null;
            switch (proxyType){
                case JDK:
                    serverProxy = Proxy.newProxyInstance(serverClass.getClassLoader(), new Class[]{serverClass},
                            (Object proxy, Method method, Object[] args) -> {
                                return intercept(proxy, method, args, null);
                            });
                case Cglib:
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(serverClass);
                    enhancer.setCallback(this);
                    serverProxy = enhancer.create();
            }
            proxy.put(serverClass.getName(),serverProxy);
        }
        return proxy.get(serverClass.getName());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if(field.getAnnotation(RPCResource.class)!=null){
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

    public enum ProxyType{
        JDK,
        Cglib
    }
}
