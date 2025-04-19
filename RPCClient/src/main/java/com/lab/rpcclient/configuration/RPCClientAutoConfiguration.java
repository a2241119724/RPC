package com.lab.rpcclient.configuration;

import com.lab.rpcclient.netty.ClientAop;
import com.lab.rpcclient.netty.NettyClient;
import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.lab.rpcclient.utils.BeanUtils;
import com.lab.rpcclient.zookeeper.IServerDiscovery;
import com.lab.rpcclient.zookeeper.ServerDiscovery;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author lab
 * @Title: ClientConfiguration
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/14 23:16
 */
@Configuration
public class RPCClientAutoConfiguration implements CommandLineRunner {
    @Bean
    @Scope("prototype")
    public NettyClientHandler getNettyClientHandler(){
        return new NettyClientHandler();
    }

    @Bean
    public NettyClient nettyClient(){
        return new NettyClient();
    }

    @Bean
    public ClientAop clientAop(){
        return new ClientAop();
    }

    @Bean
    public BeanUtils beanUtils(){
        return new BeanUtils();
    }

    @Bean
    public IServerDiscovery serverDiscovery(){
        return new ServerDiscovery();
    }

    @Override
    public void run(String... args){
        serverDiscovery().connect();
    }
}
