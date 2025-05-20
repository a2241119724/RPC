package com.lab.rpc.server.configuration;

import com.lab.rpc.server.monitor.PrometheusCustomMonitor;
import com.lab.rpc.server.netty.NettyServer;
import com.lab.rpc.server.netty.handler.NettyServerHandler;
import com.lab.rpc.server.register.IServerRegister;
import com.lab.rpc.common.utils.Utils;
import com.lab.rpc.server.netty.handler.ServerHeartBeatHandler;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author lab
 * @title RpcServerAutoConfiguration
 * @projectName RPC
 * @description 配置相关Bean
 * @date 2025/4/18 22:09
 */
@Configuration
public class RpcServerAutoConfiguration implements CommandLineRunner {
    @Resource
    private MeterRegistry meterRegistry;

    @Bean
    public PrometheusCustomMonitor prometheusCustomMonitor(){
        return new PrometheusCustomMonitor(meterRegistry);
    }

    @Bean
    public NettyServer nettyServer(){
        return new NettyServer();
    }

    @Bean
    public NettyServerHandler nettyServerHandler(){
        return new NettyServerHandler();
    }

    @Bean
    @ConditionalOnMissingBean(IServerRegister.class)
    public IServerRegister serverRegister(){
        return Utils.getInstanceBySpi(IServerRegister.class);
    }

    @Bean
    public ServerHeartBeatHandler serverHeartBeatHandler(){
        return new ServerHeartBeatHandler();
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer().start();
    }
}
