package com.lab.provider.netty.handler;

import com.alibaba.fastjson.JSON;
import com.lab.common.pojo.RPCRequest;
import com.lab.common.pojo.RPCResponse;
import com.lab.provider.annotation.RPCServer;
import com.lab.provider.monitor.PrometheusCustomMonitor;
import com.lab.provider.netty.NettyServer;
import com.lab.provider.property.NettyServerProperty;
import com.lab.provider.zookeeper.ServerRegister;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lab
 * @Title: NettyServerHandler
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 17:17
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {
    // 获取服务port
    @Resource
    private WebServerApplicationContext webServerApplicationContext;
    @Resource
    private NettyServerProperty nettyServerProperty;
    @Resource
    private PrometheusCustomMonitor monitor;

    private Map<String, Object> map = new HashMap<>();
    private long startTime;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        monitor.getReportDialRequestCount().increment();
        startTime = System.currentTimeMillis();
        super.channelActive(ctx);
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info(socketAddress.getHostName() + socketAddress.getPort() + "创建连接...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg){
        log.info("收到Consumer的消息:" + msg);
        RPCRequest request = null;
        RPCResponse.RPCResponseBuilder responseBuilder = RPCResponse.builder();
        try {
            request = JSON.parseObject(msg, RPCRequest.class);
        }catch (Exception e){
            log.info(e.getStackTrace().toString());
            ctx.writeAndFlush(JSON.toJSONString(responseBuilder.build()));
            return;
        }
        responseBuilder.requestId(request.getRequestId());
        if(!map.containsKey(request.getServerName())){
            ctx.writeAndFlush(JSON.toJSONString(responseBuilder.build()));
            return;
        }
        Object serverBean = map.get(request.getServerName());
        FastClass fastClass = FastClass.create(serverBean.getClass());
        FastMethod method = fastClass.getMethod(request.getFunctionName(), request.getParameterTypes());
        try {
            Object result = method.invoke(serverBean, request.getParameters());
            responseBuilder.result(result);
        }catch (Exception e){
            log.info(e.getStackTrace().toString());
            responseBuilder.error(e.getStackTrace().toString());
        }
        //
        ChannelFuture future = ctx.writeAndFlush(JSON.toJSONString(responseBuilder.build()) + NettyServer.DELIMITER);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                monitor.getReportDialResponseTime().record(System.currentTimeMillis()-startTime, TimeUnit.MILLISECONDS);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        if (cause instanceof ClosedChannelException) {
            log.warn("连接被对方关闭: {}", ctx.channel().remoteAddress());
        } else if (cause instanceof IOException) {
            log.error("网络异常断开: {}", cause.getMessage());
        } else {
            log.error("业务异常", cause);
        }
        ctx.close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RPCServer.class);
        ServerRegister serverRegister = ServerRegister.getInstance();
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
}
