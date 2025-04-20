package com.lab.rpcclient.netty.handler;

import com.lab.rpcclient.netty.NettyClient;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
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
public class NettyClientHandler extends SimpleChannelInboundHandler<ProtocolMessage<RPCResponse>> {
    @Resource
    private NettyClient nettyClient;

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            NettyRuntime.availableProcessors() * 2,NettyRuntime.availableProcessors() * 4,1,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(200), new DefaultThreadFactory("Request"));

    private RPCResponse response;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RPCResponse> msg){
        log.info("收到Provider的消息:" + msg.toString());
        try {
            this.response = msg.getBody();
            notify();
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    public RPCResponse send(ProtocolMessage<?> message) throws InterruptedException, ExecutionException {
        executor.submit(()->{
            synchronized (this){
                ctx.writeAndFlush(message);
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
}
