package com.lab.rpcserver.server;

import com.lab.rpcserver.property.ZKProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: ServerRegister
 * @ProjectName RPC
 * @Description: Zookeeper服务注册
 * @date 2025/4/12 22:03
 */
@Slf4j
public class ZKServerRegister implements IServerRegister {
    @Resource
    private ZKProperty ZKProperty;

    private CuratorFramework client;
    private final String NAMESPACE = "RPCServers";

    @Override
    public void connect() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString(ZKProperty.getHost())
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
}
