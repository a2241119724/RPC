package com.lab.rpc.server.netty.handler;

import com.lab.rpc.common.enumerate.ProtocolMessageTypeEnum;
import com.lab.rpc.common.message.ProtocolMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lab
 * @title ServerHeartBeatHandler
 * @projectName RPC
 * @description 心跳处理，若长时间没有读事件，则发送心跳
 * @date 2025/4/22 22:27
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHeartBeatHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ProtocolMessage.Header header = ProtocolMessage.Header.builder()
                    .type(ProtocolMessageTypeEnum.HEART_RESPONSE.getKey()).build();
                ProtocolMessage<Object> protocolMessage = new ProtocolMessage<>();
                protocolMessage.setHeader(header);
                log.info("服务器主动探测客户端连接情况");
                ctx.writeAndFlush(protocolMessage).addListener(f->{
                   if(!f.isSuccess()){
                       Channel channel = ctx.channel();
                       log.info("关闭Channel:"+channel.remoteAddress());
                       channel.close();
                   }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
