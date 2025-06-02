package com.lab.rpc.common.constant;


/**
 * @author lab
 * @title ProtocolConstant
 * @projectName RPC
 * @description 协议常量配置
 * @date 2025/4/21 0:22
 */
public interface ProtocolConstant {

    /** 消息头长度 */
    int MESSAGE_HEADER_LENGTH = 17;

    /** 协议魔数 */
    byte PROTOCOL_MAGIC = 0x1;

    /** 协议版本号 */
    byte PROTOCOL_VERSION = 0x1;

    int MAX_FRAME_LENGTH = 8192;

    int HEART_TIME = 600000;
}
