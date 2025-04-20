package com.lab.rpccommon.handler;

import com.lab.rpccommon.constant.ProtocolConstant;
import com.lab.rpccommon.enum_.ProtocolMessageSerializerEnum;
import com.lab.rpccommon.factory.ISerializer;
import com.lab.rpccommon.factory.SerializerFactory;
import com.lab.rpccommon.pojo.ProtocolMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author lab
 * @Title: RPCEncoder
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/19 22:42
 */
public class RPCEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){
        ProtocolMessage protocolMessage = (ProtocolMessage) o;
        if (o == null || protocolMessage.getHeader() == null) {
            return;
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 获得对应的序列化器
        ISerializer serializer = SerializerFactory.getInstance().getSerializer(
                ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer()).getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // head
        byteBuf.writeByte(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerializer());
        byteBuf.writeByte(header.getType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        // body
        byteBuf.writeInt(bodyBytes.length);
        byteBuf.writeBytes(bodyBytes);
    }
}
