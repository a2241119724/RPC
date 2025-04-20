package com.lab.rpccommon.enum_;

import lombok.Getter;

/**
 * @author lab
 * @Title: ProtocolMessageStatusEnum
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/21 1:04
 */
@Getter
public enum ProtocolMessageStatusEnum {
    SUCCESS(0),
    ERROR(1);

    private final byte key;

    ProtocolMessageStatusEnum(int key) {
        this.key = (byte)key;
    }

    /**
     * 根据 key 获取枚举
     * @param key
     * @return
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
