package com.lab.rpc.client.netty.handler;

import com.lab.rpc.client.netty.NettyClient;
import com.lab.rpc.client.spi.faulttolerance.IFaultTolerance;
import com.lab.rpc.common.enumerate.ProtocolMessageStatusEnum;
import com.lab.rpc.common.enumerate.ProtocolMessageTypeEnum;
import com.lab.rpc.common.message.ProtocolMessage;
import com.lab.rpc.common.message.RpcHeartResponse;
import com.lab.rpc.common.message.RpcResponse;
import com.lab.rpc.common.utils.Utils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lab
 * @title NettyClientHandler
 * @projectName RPC
 * @description 对于自定义协议的处理
 * @date 2025/4/19 23:00
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<ProtocolMessage<RpcResponse>> implements InitializingBean {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
        NettyRuntime.availableProcessors() * 2,NettyRuntime.availableProcessors() * 4,1,
        TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory("Request"));

    private Map<Long, ResponseFuture> responseFutureMap = new HashMap<>();
    private IFaultTolerance faultTolerance;
    private NettyClient nettyClient;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 预热
        ClientHeartBeatHandler.heartResponse(ctx);
        //
        this.ctx = ctx;
        nettyClient = Utils.getBean(NettyClient.class);
        faultTolerance = Utils.getBean(IFaultTolerance.class);
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RpcResponse> protocolMessage){
        log.info("收到Provider的消息:" + protocolMessage.toString());
        ResponseFuture future = responseFutureMap.get(protocolMessage.getHeader().getRequestId());
        future.setResult(protocolMessage);
        responseFutureMap.remove(protocolMessage.getHeader().getRequestId());
    }

    public RpcResponse send(ProtocolMessage<?> protocolMessage) throws InterruptedException, ExecutionException {
        ResponseFuture future = new ResponseFuture();
        responseFutureMap.put(protocolMessage.getHeader().getRequestId(), future);
        return executor.submit(()->{
            if(faultTolerance == null){ return null; }
            // 重试
            return faultTolerance.execute(() -> {
                log.info("Client发送:" + protocolMessage);
                ctx.writeAndFlush(protocolMessage);
                ProtocolMessage<RpcResponse> protocolMessage0 = future.get();
                if(ProtocolMessageStatusEnum.getEnumByKey(protocolMessage0.getHeader().getStatus())
                        == ProtocolMessageStatusEnum.ERROR){
                    throw new RuntimeException();
                }
                return protocolMessage0.getBody();
            });
        }).get();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        if (cause instanceof ClosedChannelException) {
            log.warn("连接被对方关闭: {}", ctx.channel().remoteAddress());
            nettyClient.removeConnect((InetSocketAddress) ctx.channel().remoteAddress(), this);
            ctx.close();
        } else if (cause instanceof IOException) {
            log.error("网络异常断开: {}", cause.getMessage());
            nettyClient.removeConnect((InetSocketAddress) ctx.channel().remoteAddress(), this);
            ctx.close();
        } else {
            log.error("业务异常", cause);
        }
    }

    @Override
    public boolean acceptInboundMessage(Object msg){
        return ((ProtocolMessage<?>)msg).getBody() instanceof RpcResponse;
    }

    @Override
    public void afterPropertiesSet(){
        executor.prestartAllCoreThreads();
    }

    class ResponseFuture {
        private ProtocolMessage<RpcResponse> result;

        public synchronized ProtocolMessage<RpcResponse> get() {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return result;
        }

        public synchronized void setResult(ProtocolMessage<RpcResponse> result) {
            this.result = result;
            notifyAll();
        }
    }
}
