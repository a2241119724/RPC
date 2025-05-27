package com.lab.rpc.client.discovery;

import com.lab.rpc.client.netty.NettyClient;
import com.lab.rpc.client.spi.loadbalance.ILoadBalance;
import com.lab.rpc.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @title AbstractServerDiscovery
 * @projectName RPC
 * @description 服务发现抽象
 * @date 2025/4/27 17:10
 */
@Slf4j
public abstract class AbstractServerDiscovery implements IServerDiscovery{
    @Resource
    private ILoadBalance loadBalance;

    protected NettyClient nettyClient;

    protected Map<String, List<InetSocketAddress>> serverCache = new ConcurrentHashMap<>();

    @Override
    public void connect() {
        nettyClient = Utils.getBean(NettyClient.class);
        connect0();
        loadAllServices();
        watchServices();
    }

    /**
     * 真正地连接服务
     */
    public abstract void connect0();

    @Override
    public InetSocketAddress getInstance(String serverName){
        return loadBalance.select(getAllInstance(serverName));
    }

    @Override
    public List<InetSocketAddress> getAllInstance(String serverName) {
        if(!serverCache.containsKey(serverName)){
            log.info("没有找到服务:" + serverName);
            log.info("重新加载服务");
            loadAllServices();
            return null;
        }
        return serverCache.get(serverName);
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
