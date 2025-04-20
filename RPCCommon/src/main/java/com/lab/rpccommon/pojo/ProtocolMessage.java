package com.lab.rpccommon.pojo;

import cn.hutool.core.util.IdUtil;
import com.lab.rpccommon.constant.ProtocolConstant;
import com.lab.rpccommon.enum_.ProtocolMessageSerializerEnum;
import com.lab.rpccommon.enum_.ProtocolMessageTypeEnum;
import com.lab.rpccommon.factory.ISerializer;
import com.lab.rpccommon.factory.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.Buffer;
import java.nio.ByteBuffer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    private Header header;
    private T body;

    @Data
    @Builder
    public static class Header {
        // 魔数，保证安全性
        @Builder.Default
        private byte magic = ProtocolConstant.PROTOCOL_MAGIC;
        // 版本号
        @Builder.Default
        private byte version = ProtocolConstant.PROTOCOL_VERSION;
        // 序列化器
        @Builder.Default
        private byte serializer = ProtocolMessageSerializerEnum.JSON.getKey();
        // 消息类型（请求 / 响应）
        private byte type;
        // 状态
        private byte status;
        // 请求 id
        @Builder.Default
        private long requestId = IdUtil.getSnowflakeNextId();
        // 消息体长度
        private int bodyLength;
    }

}
