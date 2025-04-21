package com.lab.rpccommon.handler;

import cn.hutool.log.Log;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCHeart;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lab
 * @Title: NettyPingHandler
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/21 21:54
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyHeartHandler extends SimpleChannelInboundHandler<ProtocolMessage<RPCHeart>> {
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage<RPCHeart> msg){
        if(!(msg.getBody() instanceof RPCHeart)){
            // 向下一个handler发送
            ctx.fireChannelRead(msg);
        }else{
            log.info("收到心跳检测:" + msg.toString());
            ctx.writeAndFlush(msg);
        }
    }
}
