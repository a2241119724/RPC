package com.lab.rpccommon.handler;

import com.lab.rpccommon.message.ProtocolMessage;
import com.lab.rpccommon.message.RPCHeartResponse;
import com.lab.rpccommon.message.RPCResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author lab
 * @Title: HeartResponseHandler
 * @ProjectName RPC
 * @Description: 心跳响应处理
 * @date 2025/4/23 0:55
 */
@ChannelHandler.Sharable
public class HeartResponseHandler extends SimpleChannelInboundHandler<ProtocolMessage<RPCHeartResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RPCHeartResponse> msg){
        //
    }

    @Override
    public boolean acceptInboundMessage(Object msg){
        return ((ProtocolMessage)msg).getBody() instanceof RPCHeartResponse;
    }
}
