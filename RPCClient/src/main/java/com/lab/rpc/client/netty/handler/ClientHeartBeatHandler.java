package com.lab.rpc.client.netty.handler;

import com.lab.rpc.common.enumerate.ProtocolMessageTypeEnum;
import com.lab.rpc.common.message.ProtocolMessage;
import com.lab.rpc.common.message.RpcHeartRequest;
import com.lab.rpc.common.message.RpcHeartResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lab
 * @title ClientHeartBeatHandler
 * @projectName RPC
 * @description 心跳处理，若长时间没有写事件，则发送心跳
 * @date 2025/4/22 22:19
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientHeartBeatHandler extends SimpleChannelInboundHandler<ProtocolMessage<RpcHeartRequest>> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                // 难点
                // 不使用send,因为会使得EventLoop被pack
                // 线程池被wait,EventLoop被线程池的get() pack
                // visualVM
                log.info("发送心跳");
                heartResponse(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 服务端主动心跳检测
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RpcHeartRequest> msg){
        ProtocolMessage.Header header = ProtocolMessage.Header.builder()
                .requestId(msg.getHeader().getRequestId())
                .type(ProtocolMessageTypeEnum.HEART_RESPONSE.getKey()).build();
        ProtocolMessage<RpcHeartResponse> protocolMessage = new ProtocolMessage<>();
        protocolMessage.setHeader(header);
        protocolMessage.setBody(RpcHeartResponse.builder().build());
        ctx.writeAndFlush(protocolMessage);
    }

    @Override
    public boolean acceptInboundMessage(Object msg){
        return ((ProtocolMessage<?>)msg).getBody() instanceof RpcHeartRequest;
    }

    static void heartResponse(ChannelHandlerContext ctx) {
        ProtocolMessage.Header header = ProtocolMessage.Header.builder()
                .type(ProtocolMessageTypeEnum.HEART_RESPONSE.getKey()).build();
        ProtocolMessage<RpcHeartResponse> protocolMessage = new ProtocolMessage<>();
        protocolMessage.setHeader(header);
        protocolMessage.setBody(RpcHeartResponse.builder().build());
        ctx.writeAndFlush(protocolMessage);
    }
}
