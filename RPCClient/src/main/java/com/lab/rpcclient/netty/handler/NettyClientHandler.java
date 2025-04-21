package com.lab.rpcclient.netty.handler;

import com.lab.rpcclient.netty.NettyClient;
import com.lab.rpcclient.spi.faulttolerance.IFaultTolerance;
import com.lab.rpccommon.enum_.ProtocolMessageStatusEnum;
import com.lab.rpccommon.enum_.ProtocolMessageTypeEnum;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCHeart;
import com.lab.rpccommon.pojo.RPCResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.IOException;
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
    @Resource
    private IFaultTolerance faultTolerance;

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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ProtocolMessage.Header header = ProtocolMessage.Header.builder()
                        .type(ProtocolMessageTypeEnum.HEART_BEAT.getKey()).build();
                ProtocolMessage<RPCHeart> protocolMessage = new ProtocolMessage();
                protocolMessage.setHeader(header);
                protocolMessage.setBody(new RPCHeart());
                // 难点
                // 不使用send,因为会使得EventLoop被pack
                // 线程池被wait,EventLoop被线程池的get() pack
                // visualVM
                ctx.writeAndFlush(protocolMessage).addListener(f->{
                    if(f.isSuccess()){
                        //
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RPCResponse> protocolMessage){
        if(ProtocolMessageTypeEnum.getEnumByKey(protocolMessage.getHeader().getType())
                == ProtocolMessageTypeEnum.HEART_BEAT){
            log.info("收到对方存活:" + protocolMessage.toString());
        }else{
            log.info("收到Provider的消息:" + protocolMessage.toString());
        }
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
        } else if (cause instanceof IOException) {
            log.error("网络异常断开: {}", cause.getMessage());
        } else {
            log.error("业务异常", cause);
        }
        ctx.close();
    }
}
