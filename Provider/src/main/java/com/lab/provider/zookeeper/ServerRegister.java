package com.lab.provider.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author lab
 * @Title: ServerRegister
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/12 22:03
 */
@Slf4j
public class ServerRegister implements IServerRegister{
    private volatile static ServerRegister Instance;
    private CuratorFramework client;
    private final String NAMESPACE = "RPCServers";

    private ServerRegister(){}

    @Override
    public void connect() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.1.111:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retry)
                .namespace(NAMESPACE)
                .build();
        client.start();
    }

    @Override
    public void register(String serverName, String address) {
        String serverPath = "/" + serverName + "/lab";
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(serverPath, address.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerRegister getInstance() {
        if(Instance == null){
            synchronized (ServerRegister.class){
                if(Instance == null){
                    Instance = new ServerRegister();
                }
            }
        }
        return Instance;
    }
}
