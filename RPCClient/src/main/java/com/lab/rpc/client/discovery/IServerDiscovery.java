package com.lab.rpc.client.discovery;

import java.net.InetSocketAddress;

/**
 * @author lab
 * @title IServerDiscovery
 * @projectName RPC
 * @description 服务发现
 * @date 2025/4/12 22:17
 */
public interface IServerDiscovery {
    /**
     * 连接注册中心
     */
    void connect();
    /**
     * 获取所有服务
     */
    void loadAllServices();
    /**
     * 监听服务
     */
    void watchServices();

    /**
     * 获取服务实例
     * @param serverName 服务接口名称
     * @return 服务实例地址
     */
    InetSocketAddress getInstance(String serverName);
}
