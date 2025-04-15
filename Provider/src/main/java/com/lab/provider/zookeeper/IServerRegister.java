package com.lab.provider.zookeeper;

/**
 * @author lab
 * @Title: IServerRegister
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/12 22:02
 */
public interface IServerRegister {
    void connect();

    void register(String serverName, String address);
}
