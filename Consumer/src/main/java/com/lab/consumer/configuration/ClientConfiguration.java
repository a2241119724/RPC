package com.lab.consumer.configuration;

import com.lab.consumer.netty.handler.NettyClientHandler;
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
public class ClientConfiguration {
    @Bean
    @Scope("prototype")
    public NettyClientHandler getNettyClientHandler(){
        return new NettyClientHandler();
    }
}
