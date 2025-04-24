package com.lab.rpccommon.enum_;

import lombok.Getter;

/**
 * @author lab
 * @Title: ProtocolMessageTypeEnum
 * @ProjectName RPC
 * @Description: 自定义协议类型
 * @date 2025/4/21 1:04
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_REQUEST(2),
    HEART_RESPONSE(3),
    OTHERS(4);

    private final byte key;

    ProtocolMessageTypeEnum(int key) {
        this.key = (byte)key;
    }

    /**
     * 根据 key 获取枚举
     * @param key
     * @return
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
