package com.lab.rpc.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lab
 * @title RpcResponse
 * @projectName RPC
 * @description 自定义响应协议Body
 * @date 2025/4/9 19:35
 */
@Data
@Builder
@AllArgsConstructor
public class RpcResponse implements Serializable {
    private Object result;
    private String error;
}
