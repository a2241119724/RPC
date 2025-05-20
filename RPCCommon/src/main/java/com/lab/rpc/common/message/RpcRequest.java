package com.lab.rpc.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lab
 * @title RpcRequest
 * @projectName RPC
 * @description 自定义请求协议Body
 * @date 2025/4/9 19:35
 */
@Data
@Builder
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private String serverName;
    private String functionName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
