package com.lab.rpc.server.register;

import com.lab.rpc.server.property.ZooKeeperProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import javax.annotation.Resource;

/**
 * @author lab
 * @title ServerRegister
 * @projectName RPC
 * @description Zookeeper服务注册
 * 注意:需要再hosts文件中配置ZK的ip->host,不然会延迟10s左右
 * @date 2025/4/12 22:03
 */
@Slf4j
public class ZooKeeperServerRegister implements IServerRegister {
    @Resource
    private ZooKeeperProperty zooKeeperProperty;

    private CuratorFramework client;
    private static final String NAMESPACE = "RPCServers";

    @Override
    public void connect() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperProperty.getHost())
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
