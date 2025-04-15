package com.lab.consumer.zookeeper;

/**
 * @author lab
 * @Title: IServerDiscovery
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/12 22:17
 */
public interface IServerDiscovery {
    void connect();
    void loadAllServices();
    void watchServices();
}
