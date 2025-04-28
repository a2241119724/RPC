package com.lab.rpccommon.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lab
 * @Title: RPCResponse
 * @ProjectName RPC
 * @Description: 自定义响应协议Body
 * @date 2025/4/9 19:35
 */
@Data
@Builder
@AllArgsConstructor
public class RPCResponse implements Serializable {
    private Object result;
    private String error;
}
