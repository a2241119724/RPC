package com.lab.rpc.common.message;

import cn.hutool.core.util.IdUtil;
import com.lab.rpc.common.constant.ProtocolConstant;
import com.lab.rpc.common.enumerate.ProtocolMessageStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param <T> 协议中Body类型
 * @author lab
 * @title ProtocolMessage
 * @projectName RPC
 * @description 协议结构
 * @date 2025/4/19 22:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T>{
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
        // 消息类型 (请求 / 响应 / 心跳)
        private byte type;
        // 响应状态 (成功 / 失败)
        @Builder.Default
        private byte status = ProtocolMessageStatusEnum.SUCCESS.getKey();
        // 请求 id
        @Builder.Default
        private long requestId = IdUtil.getSnowflakeNextId();
        // 消息体长度
        private int bodyLength;
    }

}
