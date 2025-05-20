package com.lab.rpc.common.handler;

import com.lab.rpc.common.message.ProtocolMessage;
import com.lab.rpc.common.message.RpcHeartResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author lab
 * @title HeartResponseHandler
 * @projectName RPC
 * @description 心跳响应处理
 * @date 2025/4/23 0:55
 */
@ChannelHandler.Sharable
public class HeartResponseHandler extends SimpleChannelInboundHandler<ProtocolMessage<RpcHeartResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RpcHeartResponse> msg){
        //
    }

    @Override
    public boolean acceptInboundMessage(Object msg){
        return ((ProtocolMessage<?>)msg).getBody() instanceof RpcHeartResponse;
    }
}
