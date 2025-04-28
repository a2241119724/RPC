package com.lab.rpcclient.server.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.lab.rpcclient.server.AServerDiscovery;
import com.lab.rpcclient.server.IServerDiscovery;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author lab
 * @Title: NacosServerDiscovery
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/26 23:06
 */
public class NacosServerDiscovery extends AServerDiscovery {
    private NamingService namingService;

    @Override
    public void connect0() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, "192.168.1.120:8858");
        properties.put(PropertyKeyConst.USERNAME, "nacos");
        properties.put(PropertyKeyConst.PASSWORD, "nacos");
        properties.put(PropertyKeyConst.NAMESPACE, "public");
        try {
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadAllServices() {
        ListView<String> servicesOfServer = null;
        try {
            servicesOfServer = namingService.getServicesOfServer(1, 100);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        for (String server : servicesOfServer.getData()) {
            List<Instance> instances = null;
            try {
                instances = namingService.getAllInstances(server);
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
            List<InetSocketAddress> addresses = new ArrayList();
            for (Instance instance : instances){
                addresses.add(new InetSocketAddress(instance.getIp(), instance.getPort()));
            }
            serverCache.put(server, addresses);
        }
        info();
    }

    @Override
    public void watchServices() {
        ListView<String> servicesOfServer = null;
        try {
            servicesOfServer = namingService.getServicesOfServer(1, 100);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
        for (String server : servicesOfServer.getData()) {
            try {
                namingService.subscribe(server, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        NamingEvent namingEvent = (NamingEvent) event;
                        List<InetSocketAddress> addresses = new ArrayList();
                        for (Instance instance : namingEvent.getInstances()) {
                            addresses.add(new InetSocketAddress(instance.getIp(), instance.getPort()));
                        }
                        serverCache.put(server, addresses);
                        info();
                    }
                });
            } catch (NacosException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
