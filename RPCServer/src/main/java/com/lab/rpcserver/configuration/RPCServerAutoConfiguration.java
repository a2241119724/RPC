package com.lab.rpcserver.configuration;

import com.lab.rpccommon.utils.Utils;
import com.lab.rpcserver.monitor.PrometheusCustomMonitor;
import com.lab.rpcserver.netty.NettyServer;
import com.lab.rpcserver.netty.handler.NettyServerHandler;
import com.lab.rpcserver.netty.handler.ServerHeartBeatHandler;
import com.lab.rpcserver.server.IServerRegister;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: RPCServerAutoConfiguration
 * @ProjectName RPC
 * @Description: 配置相关Bean
 * @date 2025/4/18 22:09
 */
@Configuration
public class RPCServerAutoConfiguration implements CommandLineRunner {
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
        return Utils.getInstanceBySPI(IServerRegister.class);
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
