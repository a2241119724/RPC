package com.lab.rpcserver.server;

/**
 * @author lab
 * @Title: IServerRegister
 * @ProjectName RPC
 * @Description: 服务注册
 * @date 2025/4/12 22:02
 */
public interface IServerRegister {
    void connect();

    void register(String serverName, String address);
}
