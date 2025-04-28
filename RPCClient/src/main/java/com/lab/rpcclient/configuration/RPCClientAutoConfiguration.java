package com.lab.rpcclient.configuration;

import com.lab.rpcclient.netty.ClientAop;
import com.lab.rpcclient.netty.NettyClient;
import com.lab.rpcclient.netty.handler.ClientHeartBeatHandler;
import com.lab.rpcclient.spi.faulttolerance.IFaultTolerance;
import com.lab.rpcclient.spi.loadbalance.ILoadBalance;
import com.lab.rpcclient.xxl.MyJobHandler;
import com.lab.rpccommon.utils.Utils;
import com.lab.rpcclient.server.IServerDiscovery;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author lab
 * @Title: ClientConfiguration
 * @ProjectName RPC
 * @Description: 配置相关的Bean
 * @date 2025/4/14 23:16
 */
@Configuration
public class RPCClientAutoConfiguration implements CommandLineRunner {
//    @Bean
//    @Scope("prototype")
//    public NettyClientHandler getNettyClientHandler(){
//        return new NettyClientHandler();
//    }

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
        return Utils.getInstanceBySPI(ILoadBalance.class);
    }

    @Bean
    @ConditionalOnMissingBean(IServerDiscovery.class)
    public IServerDiscovery serverDiscovery(){
        return Utils.getInstanceBySPI(IServerDiscovery.class);
    }

    /**
     * 由于prototype和ConditionalOnMissingBean，不能添加多个
     * 在创建第一个Bean之后，满足了Conditional
     * @return
     */
    @Bean
    @Scope("prototype")
//    @ConditionalOnMissingBean(IFaultTolerance.class)
    public IFaultTolerance faultTolerance(){
        return Utils.getInstanceBySPI(IFaultTolerance.class);
    }

    @Bean
    public MyJobHandler jobHandler(){
        return new MyJobHandler();
    }

    @Bean
    public ClientHeartBeatHandler clientHeartBeatHandler(){
        return new ClientHeartBeatHandler();
    }

    @Override
    public void run(String... args){
        serverDiscovery().connect();
    }
}
