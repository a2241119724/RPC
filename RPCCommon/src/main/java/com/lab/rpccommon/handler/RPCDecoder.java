package com.lab.rpccommon.handler;

import com.lab.rpccommon.constant.ProtocolConstant;
import com.lab.rpccommon.enum_.ProtocolMessageSerializerEnum;
import com.lab.rpccommon.enum_.ProtocolMessageTypeEnum;
import com.lab.rpccommon.factory.ISerializer;
import com.lab.rpccommon.factory.SerializerFactory;
import com.lab.rpccommon.pojo.ProtocolMessage;
import com.lab.rpccommon.pojo.RPCRequest;
import com.lab.rpccommon.pojo.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.List;

/**
 * @author lab
 * @Title: RPCDecoder
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/19 22:43
 */
public class RPCDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
        // 校验魔数
        byte magic = byteBuf.readByte();
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        ProtocolMessage.Header header = ProtocolMessage.Header.builder().magic(magic)
                .version(byteBuf.readByte())
                .serializer(byteBuf.readByte())
                .type(byteBuf.readByte())
                .status(byteBuf.readByte())
                .requestId(byteBuf.readLong())
                .bodyLength(byteBuf.readInt()).build();
        byte[] bodyBytes = new byte[header.getBodyLength()];
        byteBuf.readBytes(bodyBytes, 0, header.getBodyLength());
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }
        ISerializer serializer = SerializerFactory.getInstance().getSerializer(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RPCRequest request = serializer.deserialize(bodyBytes, RPCRequest.class);
                list.add(new ProtocolMessage(header, request));
                break;
            case RESPONSE:
                RPCResponse response = serializer.deserialize(bodyBytes, RPCResponse.class);
                list.add(new ProtocolMessage(header, response));
                break;
            case HEART_BEAT:
            case OTHERS:
                break;
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
