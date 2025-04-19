package com.lab.rpccommon.pojo;

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
    private String requestId;
    private String serverName;
    private String functionName;
    private Class[] parameterTypes;
    private Object[] parameters;
}
