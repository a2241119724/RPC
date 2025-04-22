package com.lab.rpccommon.handler;

import com.lab.rpccommon.spi.ISerializer;
import com.lab.rpccommon.message.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: RPCEncoder
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/19 22:42
 */
@Slf4j
@ChannelHandler.Sharable
public class RPCEncoder extends MessageToByteEncoder {
    @Resource
    private ISerializer serializer;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){
        ProtocolMessage protocolMessage = (ProtocolMessage) o;
        if (o == null || protocolMessage.getHeader() == null) {
            return;
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // head
        byteBuf.writeByte(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        // body
        byteBuf.writeInt(bodyBytes.length);
        byteBuf.writeBytes(bodyBytes);
    }
}
