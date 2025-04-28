package com.lab.rpcclient.server;

import com.lab.rpcclient.spi.loadbalance.ILoadBalance;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @Title: AServerDiscovery
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/27 17:10
 */
@Slf4j
public abstract class AServerDiscovery implements IServerDiscovery{
    @Resource
    private ILoadBalance loadBalance;

    protected Map<String, List<InetSocketAddress>> serverCache = new ConcurrentHashMap<>();

    @Override
    public void connect() {
        connect0();
        loadAllServices();
        watchServices();
    }

    public abstract void connect0();

    public InetSocketAddress getInstance(String serverName){
        if(!serverCache.containsKey(serverName)){
            log.info("没有找到服务:" + serverName);
            return null;
        }
        List<InetSocketAddress> addresses = serverCache.get(serverName);
        return loadBalance.select(addresses);
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
}
