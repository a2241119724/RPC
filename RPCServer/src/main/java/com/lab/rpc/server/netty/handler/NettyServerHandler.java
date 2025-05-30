package com.lab.rpc.server.netty.handler;

import com.lab.rpc.common.message.RpcRequest;
import com.lab.rpc.server.monitor.PrometheusCustomMonitor;
import com.lab.rpc.server.property.NettyServerProperty;
import com.lab.rpc.server.register.IServerRegister;
import com.lab.rpc.common.enumerate.ProtocolMessageStatusEnum;
import com.lab.rpc.common.enumerate.ProtocolMessageTypeEnum;
import com.lab.rpc.common.message.ProtocolMessage;
import com.lab.rpc.common.message.RpcResponse;
import com.lab.rpc.server.annotation.RPCServer;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author lab
 * @title NettyServerHandler
 * @projectName RPC
 * @description 自定义协议的处理
 * @date 2025/4/9 17:17
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<ProtocolMessage<RpcRequest>> implements ApplicationContextAware {
    /** 获取服务port */
    @Resource
    private WebServerApplicationContext webServerApplicationContext;
    @Resource
    private NettyServerProperty nettyServerProperty;
    @Resource
    private PrometheusCustomMonitor monitor;
    @Resource
    private IServerRegister serverRegister;

    private final Map<String, Object> map = new HashMap<>();
    private final Map<String, FastClass> fastMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        monitor.getReportDialRequestCount().increment();
        super.channelActive(ctx);
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info(socketAddress.getHostName() + socketAddress.getPort() + "创建连接...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RpcRequest> protocolMessage){
        log.info("收到Consumer的消息:" + protocolMessage.toString());
        final long startTime = System.currentTimeMillis();
        RpcRequest request = protocolMessage.getBody();
        RpcResponse.RpcResponseBuilder responseBuilder = RpcResponse.builder();
        ProtocolMessage.Header.HeaderBuilder header = ProtocolMessage.Header.builder()
                .requestId(protocolMessage.getHeader().getRequestId())
                .type(ProtocolMessageTypeEnum.RESPONSE.getKey());
        ProtocolMessage<RpcResponse> protocolMessage0 = new ProtocolMessage<>();
        if(!map.containsKey(request.getServerName())){
            protocolMessage0.setHeader(header.status(ProtocolMessageStatusEnum.ERROR.getKey()).build());
            protocolMessage0.setBody(responseBuilder.build());
            ctx.writeAndFlush(protocolMessage0);
            return;
        }
        Object serverBean = map.get(request.getServerName());
        FastClass fastClass = fastMap.getOrDefault(request.getServerName(), FastClass.create(serverBean.getClass()));
        FastMethod method = fastClass.getMethod(request.getFunctionName(), request.getParameterTypes());
        try {
            Object result = method.invoke(serverBean, request.getParameters());
            responseBuilder.result(result);
        }catch (Exception e){
            log.info(Arrays.toString(e.getStackTrace()));
            responseBuilder.error(Arrays.toString(e.getStackTrace()));
        }
        //
        protocolMessage0.setHeader(header.build());
        protocolMessage0.setBody(responseBuilder.build());
        ctx.writeAndFlush(protocolMessage0).addListener((ChannelFutureListener) channelFuture -> monitor.getReportDialResponseTime().record(System.currentTimeMillis()-startTime, TimeUnit.MILLISECONDS));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        if (cause instanceof ClosedChannelException) {
            log.warn("连接被对方关闭: {}", ctx.channel().remoteAddress());
            ctx.close();
        } else if (cause instanceof IOException) {
            log.error("网络异常断开: {}", cause.getMessage());
            ctx.close();
        } else {
            log.error("业务异常", cause);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RPCServer.class);
        serverRegister.connect();
        beans.forEach((a, b)->{
            String serverName = b.getClass().getInterfaces()[0].getSimpleName();
            map.put(b.getClass().getInterfaces()[0].getSimpleName(), b);
            try {
                serverRegister.register(serverName,
                        Inet4Address.getLocalHost().getHostAddress()
                        + ":" + nettyServerProperty.getPort());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean acceptInboundMessage(Object msg){
        return ((ProtocolMessage<?>)msg).getBody() instanceof RpcRequest;
    }
}
