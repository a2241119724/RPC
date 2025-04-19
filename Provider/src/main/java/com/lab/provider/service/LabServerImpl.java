package com.lab.provider.service;

import com.lab.common.api.LabServer;
import com.lab.rpcserver.annotation.RPCServer;

/**
 * @author lab
 * @Title: LabServiceImpl
 * @ProjectName RPC
 * @Description: TODO
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
