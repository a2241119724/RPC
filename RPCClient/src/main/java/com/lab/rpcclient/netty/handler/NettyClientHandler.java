package com.lab.rpcclient.netty.handler;

import com.lab.rpcclient.netty.NettyClient;
import com.lab.rpcclient.spi.faulttolerance.IFaultTolerance;
import com.lab.rpccommon.enum_.ProtocolMessageStatusEnum;
import com.lab.rpccommon.message.ProtocolMessage;
import com.lab.rpccommon.message.RPCHeartRequest;
import com.lab.rpccommon.message.RPCResponse;
import com.lab.rpccommon.utils.Utils;
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
 * @Title: NettyClientHandler
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/19 23:00
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<ProtocolMessage<RPCResponse>> {
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            NettyRuntime.availableProcessors() * 2,NettyRuntime.availableProcessors() * 4,1,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(200), new DefaultThreadFactory("Request"));

    private IFaultTolerance faultTolerance;
    private NettyClient nettyClient;
    private RPCResponse response;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        nettyClient = Utils.getBean(NettyClient.class);
        faultTolerance = Utils.getBean(IFaultTolerance.class);
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RPCResponse> protocolMessage){
        log.info("收到Provider的消息:" + protocolMessage.toString());
        try {
            response = protocolMessage.getBody();
            notify();
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    public RPCResponse send(ProtocolMessage<?> protocolMessage) throws InterruptedException, ExecutionException {
        executor.submit(()->{
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
            nettyClient.removeConnect((InetSocketAddress) ctx.channel().remoteAddress());
            ctx.close();
        } else if (cause instanceof IOException) {
            log.error("网络异常断开: {}", cause.getMessage());
            nettyClient.removeConnect((InetSocketAddress) ctx.channel().remoteAddress());
            ctx.close();
        } else {
            log.error("业务异常", cause);
        }
    }

    @Override
    public boolean acceptInboundMessage(Object msg){
        return ((ProtocolMessage)msg).getBody() instanceof RPCResponse;
    }
}
