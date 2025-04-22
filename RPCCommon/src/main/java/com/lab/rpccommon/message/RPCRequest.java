package com.lab.rpccommon.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author lab
 * @Title: RPCRequest
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 19:35
 */
@Data
@Builder
@AllArgsConstructor
public class RPCRequest {
    private String serverName;
    private String functionName;
    private Class[] parameterTypes;
    private Object[] parameters;
}
