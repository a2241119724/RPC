package com.lab.rpcserver.configuration;

import com.lab.rpcserver.monitor.PrometheusCustomMonitor;
import com.lab.rpcserver.netty.NettyServer;
import com.lab.rpcserver.netty.handler.NettyServerHandler;
import com.lab.rpcserver.zookeeper.ServerRegister;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: RPCServerAutoConfiguration
 * @ProjectName RPC
 * @Description: TODO
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
    public ServerRegister serverRegister(){
        return new ServerRegister();
    }


    @Override
    public void run(String... args) throws Exception {
        nettyServer().start();
    }
}
