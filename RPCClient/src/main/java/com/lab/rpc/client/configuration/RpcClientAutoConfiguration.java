package com.lab.rpc.client.configuration;

import com.lab.rpc.client.netty.ClientAop;
import com.lab.rpc.client.netty.NettyClient;
import com.lab.rpc.client.netty.handler.ClientHeartBeatHandler;
import com.lab.rpc.client.spi.faulttolerance.IFaultTolerance;
import com.lab.rpc.client.spi.loadbalance.ILoadBalance;
import com.lab.rpc.client.xxl.MyJobHandler;
import com.lab.rpc.common.utils.Utils;
import com.lab.rpc.client.discovery.IServerDiscovery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author lab
 * @title ClientConfiguration
 * @projectName RPC
 * @description 配置相关的Bean
 * @date 2025/4/14 23:16
 */
@Configuration
public class RpcClientAutoConfiguration{
    @Bean
    public NettyClient nettyClient(){
        return new NettyClient();
    }

    @Bean
    public ClientAop clientAop(){
        return new ClientAop();
    }

    @Bean
    @ConditionalOnMissingBean(ILoadBalance.class)
    public ILoadBalance loadBalance(){
        return Utils.getInstanceBySpi(ILoadBalance.class);
    }

    @Bean
    @ConditionalOnMissingBean(IServerDiscovery.class)
    public IServerDiscovery serverDiscovery(){
        return Utils.getInstanceBySpi(IServerDiscovery.class);
    }

    /**
     * 由于prototype和ConditionalOnMissingBean，不能添加多个
     * 在创建第一个Bean之后，满足了Conditional
     */
    @Bean
    @Scope("prototype")
    public IFaultTolerance faultTolerance(){
        return Utils.getInstanceBySpi(IFaultTolerance.class);
    }

    @Bean
    public MyJobHandler jobHandler(){
        return new MyJobHandler();
    }

    @Bean
    public ClientHeartBeatHandler clientHeartBeatHandler(){
        return new ClientHeartBeatHandler();
    }
}
