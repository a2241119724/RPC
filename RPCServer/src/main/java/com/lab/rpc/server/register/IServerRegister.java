package com.lab.rpc.server.register;

/**
 * @author lab
 * @title IServerRegister
 * @projectName RPC
 * @description 服务注册
 * @date 2025/4/12 22:02
 */
public interface IServerRegister {
    /**
     * 连接注册中心
     */
    void connect();

    /**
     * 注册服务
     * @param serverName 服务接口名称
     * @param address 服务地址
     */
    void register(String serverName, String address);
}
