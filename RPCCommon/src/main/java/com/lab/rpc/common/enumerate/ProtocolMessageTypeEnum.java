package com.lab.rpc.common.enumerate;

import lombok.Getter;

/**
 * @author lab
 * @title ProtocolMessageTypeEnum
 * @projectName RPC
 * @description 自定义协议类型
 * @date 2025/4/21 1:04
 */
@Getter
public enum ProtocolMessageTypeEnum {
    // 请求响应
    REQUEST(0),
    // 请求响应
    RESPONSE(1),
    // 心跳请求
    HEART_REQUEST(2),
    // 心跳响应
    HEART_RESPONSE(3),
    // 其他
    OTHERS(4);

    private final byte key;

    ProtocolMessageTypeEnum(int key) {
        this.key = (byte)key;
    }

    /**
     * 根据 key 获取枚举
     * @param key 枚举int
     * @return 具体枚举类型
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }
}
