package com.lab.rpc.client.discovery;

import com.lab.rpc.client.property.RegisterCenterProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lab
 * @title ServerDiscovery
 * @projectName RPC
 * @description Zookeeper服务发现
 * @date 2025/4/12 22:19
 */
@Slf4j
public class ZookeeperServerDiscovery extends AbstractServerDiscovery {
    @Resource
    private RegisterCenterProperty registerCenterProperty;

    private CuratorFramework client;
    private static final String NAMESPACE = "RPCServers";

    @Override
    public void connect0() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString(registerCenterProperty.getHost())
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retry)
                .namespace(NAMESPACE)
                .build();
        client.start();
    }

    @Override
    public void loadAllServices() {
        try {
            List<String> servers = client.getChildren().forPath("/");
            for (String server : servers) {
                List<String> instances = client.getChildren().forPath("/" + server);
                List<InetSocketAddress> addresses = new ArrayList<>();
                for (String instance : instances){
                    instance = new String(client.getData().forPath("/" + server + "/" + instance));
                    String[] address = instance.split(":");
                    addresses.add(new InetSocketAddress(address[0], Integer.parseInt(address[1])));
                }
                serverCache.put(server, addresses);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        info();
    }

    @Override
    public void watchServices() {
        serverCache.keySet().forEach((String serverName)->{
            try {
                client.getChildren().usingWatcher(new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                            loadAllServices();
                            try {
                                client.getChildren().usingWatcher(this).forPath("/" + serverName);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            nettyClient.preCreateConnection(serverName);
                            info();
                        }
                    }
                }).forPath("/" + serverName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
