package com.lab.rpcserver.server;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lab.rpcserver.property.NacosProperty;
import com.lab.rpcserver.server.IServerRegister;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author lab
 * @Title: NacosServerRegister
 * @ProjectName RPC
 * @Description: Nacos服务注册
 * @date 2025/4/26 17:01
 */
public class NacosServerRegister implements IServerRegister {
    @Resource
    private NacosProperty nacosProperty;

    private NamingService namingService;
    private final String NAMESPACE = "public";

    @Override
    public void connect() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosProperty.getHost());
        properties.put(PropertyKeyConst.USERNAME, nacosProperty.getUsername());
        properties.put(PropertyKeyConst.PASSWORD, nacosProperty.getPassword());
        properties.put(PropertyKeyConst.NAMESPACE, NAMESPACE);
        try {
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(String serverName, String address) {
        String[] split = address.split(":");
        Instance instance = new Instance();
        instance.setIp(split[0]);
        instance.setPort(Integer.parseInt(split[1]));
        instance.setServiceName(serverName);
        instance.setWeight(1.0);
        instance.setHealthy(true);
        instance.setClusterName("DEFAULT");
        instance.setMetadata(new HashMap<>());
        try {
            namingService.registerInstance(serverName, instance);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
