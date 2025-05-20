package com.lab.provider.service;

import com.lab.common.api.LabServer;
import com.lab.rpc.server.annotation.RPCServer;

/**
 * @author lab
 * @title LabServiceImpl
 * @projectName RPC
 * @description 提供的具体服务
 * @date 2025/4/9 16:04
 */
@RPCServer
public class LabServerImpl implements LabServer {
    @Override
    public String getInfo() {
        return "LAB RPC";
    }

    @Override
    public String getInfo(String str) {
        return "RPC " + str;
    }
}
