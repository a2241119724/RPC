package com.lab.rpcclient.netty.handler;

import com.alibaba.fastjson.JSON;
import com.lab.rpcclient.annotation.RPCResource;
import com.lab.rpcclient.netty.NettyClient;
import com.lab.rpcclient.netty.ClientAop;
import com.lab.rpccommon.pojo.RPCRequest;
import com.lab.rpccommon.pojo.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lab
 * @Title: NettyClientHandler
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 19:06
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> implements BeanPostProcessor{
    @Resource
    private NettyClient nettyClient;
    @Resource
    private ClientAop clientAop;

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(12,24,1,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), new DefaultThreadFactory("Request"));

    private Map<String, Object> map = new HashMap<>();
    private RPCResponse response;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, String msg){
        log.info("收到Provider的消息:" + msg);
        try {
            this.response = JSON.parseObject(msg, RPCResponse.class);
            notify();
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    private Object getProxy(Class serverClass, ProxyType proxyType){
        if(!map.containsKey(serverClass.getName())){
            Object serverProxy = null;
            switch (proxyType){
                case JDK:
                    serverProxy = Proxy.newProxyInstance(serverClass.getClassLoader(), new Class[]{serverClass},
                        (Object proxy, Method method, Object[] args) -> {
                            return clientAop.intercept(proxy, method, args, null);
                        });
                case Cglib:
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(serverClass);
                    enhancer.setCallback(clientAop);
                    serverProxy = enhancer.create();
            }
            map.put(serverClass.getName(),serverProxy);
        }
        return map.get(serverClass.getName());
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

    public RPCResponse send(RPCRequest request) throws InterruptedException, ExecutionException {
        executor.submit(()->{
            synchronized (this){
                ctx.writeAndFlush(JSON.toJSONString(request) + NettyClient.DELIMITER);
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).get();
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        if (cause instanceof ClosedChannelException) {
            log.warn("连接被对方关闭: {}", ctx.channel().remoteAddress());
            nettyClient.removeConnection((InetSocketAddress) ctx.channel().remoteAddress());
        } else if (cause instanceof IOException) {
            log.error("网络异常断开: {}", cause.getMessage());
            nettyClient.removeConnection((InetSocketAddress) ctx.channel().remoteAddress());
        } else {
            log.error("业务异常", cause);
        }
        ctx.close();
    }

    public enum ProxyType{
        JDK,
        Cglib
    }
}
