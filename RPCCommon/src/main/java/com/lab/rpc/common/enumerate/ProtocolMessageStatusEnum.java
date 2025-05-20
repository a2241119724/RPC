package com.lab.rpc.common.enumerate;

import lombok.Getter;

/**
 * @author lab
 * @title ProtocolMessageStatusEnum
 * @projectName RPC
 * @description 自定义协议状态
 * @date 2025/4/21 1:04
 */
@Getter
public enum ProtocolMessageStatusEnum {
    // 成功
    SUCCESS(0),
    // 失败
    ERROR(1);

    private final byte key;

    ProtocolMessageStatusEnum(int key) {
        this.key = (byte)key;
    }

    /**
     * 根据 key 获取枚举
     * @param key 枚举int
     * @return  具体枚举类型
     */
    public static ProtocolMessageStatusEnum getEnumByKey(int key) {
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }
}
