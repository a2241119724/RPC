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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
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
public class NettyClientHandler extends SimpleChannelInboundHandler<ProtocolMessage<RpcResponse>> {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
        NettyRuntime.availableProcessors() * 2,NettyRuntime.availableProcessors() * 4,1,
        TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory("Request"));

    private IFaultTolerance faultTolerance;
    private NettyClient nettyClient;
    private RpcResponse response;
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
        try {
            response = protocolMessage.getBody();
            notify();
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    public RpcResponse send(ProtocolMessage<?> protocolMessage) throws InterruptedException, ExecutionException {
        executor.submit(()->{
            if(faultTolerance == null){ return; }
            // 重试
            faultTolerance.execute(() -> {
                log.info("Client发送:" + protocolMessage.toString());
                synchronized (this){
                    ctx.writeAndFlush(protocolMessage);
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(ProtocolMessageStatusEnum.getEnumByKey(protocolMessage.getHeader().getStatus())
                            == ProtocolMessageStatusEnum.ERROR){
                        throw new RuntimeException();
                    }
                }
            });
        }).get();
        return response;
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
}
