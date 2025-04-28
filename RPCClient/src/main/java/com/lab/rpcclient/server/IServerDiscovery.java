package com.lab.rpcclient.server;

import java.net.InetSocketAddress;

/**
 * @author lab
 * @Title: IServerDiscovery
 * @ProjectName RPC
 * @Description: 服务发现
 * @date 2025/4/12 22:17
 */
public interface IServerDiscovery {
    void connect();
    void loadAllServices();
    void watchServices();
    InetSocketAddress getInstance(String serverName);
}
