package com.lab.rpc.common.handler;

import com.lab.rpc.common.enumerate.ProtocolMessageTypeEnum;
import com.lab.rpc.common.message.*;
import com.lab.rpc.common.constant.ProtocolConstant;
import com.lab.rpc.common.serializer.ISerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lab
 * @title RpcDecoder
 * @projectName RPC
 * @description 通过Type解码协议
 * @date 2025/4/19 22:43
 */
@ChannelHandler.Sharable
public class RpcDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Resource
    private ISerializer serializer;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
        // 校验魔数
        byte magic = byteBuf.readByte();
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        ProtocolMessage.Header header = ProtocolMessage.Header.builder().magic(magic)
                .version(byteBuf.readByte())
                .type(byteBuf.readByte())
                .status(byteBuf.readByte())
                .requestId(byteBuf.readLong())
                .bodyLength(byteBuf.readInt()).build();
        byte[] bodyBytes = new byte[header.getBodyLength()];
        byteBuf.readBytes(bodyBytes, 0, header.getBodyLength());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("消息的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                list.add(new ProtocolMessage<>(header, request));
                break;
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                list.add(new ProtocolMessage<>(header, response));
                break;
            case HEART_REQUEST:
                RpcHeartRequest heartRequest = serializer.deserialize(bodyBytes, RpcHeartRequest.class);
                list.add(new ProtocolMessage<>(header, heartRequest));
                break;
            case HEART_RESPONSE:
                RpcHeartResponse heartResponse = serializer.deserialize(bodyBytes, RpcHeartResponse.class);
                list.add(new ProtocolMessage<>(header, heartResponse));
                break;
            case OTHERS:
                break;
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
