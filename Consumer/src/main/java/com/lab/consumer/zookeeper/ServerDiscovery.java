package com.lab.consumer.zookeeper;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.stereotype.Controller;

import java.net.InetSocketAddress;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @Title: ServerDiscovery
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/12 22:19
 */
@Slf4j
@Controller
public class ServerDiscovery implements IServerDiscovery{
    private volatile static ServerDiscovery Instance;
    private Map<String, List<InetSocketAddress>> serverCache = new ConcurrentHashMap<>();
    private CuratorFramework client;
    private final String NAMESPACE = "RPCServers";
    private Random random = new Random();

    private ServerDiscovery(){}

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
        loadAllServices();
        watchServices();
    }

    @Override
    public void loadAllServices() {
        try {
            List<String> servers = client.getChildren().forPath("/");
            for (String server : servers) {
                List<String> instances = client.getChildren().forPath("/" + server);
                List<InetSocketAddress> addresses = new ArrayList();
                for (String instance : instances){
                    instance = new String(client.getData().forPath("/" + server + "/" + instance));
                    String[] address = instance.split(":");
                    addresses.add(new InetSocketAddress(address[0], Integer.valueOf(address[1])));
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
                        }
                    }
                }).forPath("/" + serverName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public InetSocketAddress getInstanceByRandom(String serverName){
        if(!serverCache.containsKey(serverName)){
            log.info("没有找到服务:" + serverName);
            return null;
        }
        List<InetSocketAddress> addresses = serverCache.get(serverName);
        return addresses.get(random.nextInt(addresses.size()));
    }

    public void info() {
        serverCache.forEach((key, value)->{
            System.out.println("==============================");
            System.out.println(key);
            for (InetSocketAddress address : value) {
                System.out.println(address.getAddress() + " " + address.getPort());
            }
        });
    }

    public static ServerDiscovery getInstance() {
        if(Instance == null){
            synchronized (ServerDiscovery.class){
                if(Instance == null){
                    Instance = new ServerDiscovery();
                }
            }
        }
        return Instance;
    }
}
