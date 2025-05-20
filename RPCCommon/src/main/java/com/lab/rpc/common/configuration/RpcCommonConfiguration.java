package com.lab.rpc.common.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.lab.rpc.common.handler.HeartResponseHandler;
import com.lab.rpc.common.handler.RpcDecoder;
import com.lab.rpc.common.utils.Utils;
import com.lab.rpc.common.handler.RpcEncoder;
import com.lab.rpc.common.spi.ISerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lab
 * @title RpcCommonConfiguration
 * @projectName RPC
 * @description 注册公共的Bean
 * @date 2025/4/21 0:22
 */
@Configuration
public class RpcCommonConfiguration {
    @Bean
    @ConditionalOnMissingBean(ISerializer.class)
    public ISerializer serializer(){
        return Utils.getInstanceBySpi(ISerializer.class);
    }

    @Bean
    public RpcEncoder rpcEncoder(){
        return new RpcEncoder();
    }

    @Bean
    public RpcDecoder rpcDecoder(){
        return new RpcDecoder();
    }

    @Bean
    public HeartResponseHandler heartResponseHandler(){
        return new HeartResponseHandler();
    }

    @Bean
    public Utils utils(){
        return new Utils();
    }

    @Bean
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        properties.setRegisterEnabled(false);
        return properties;
    }
}
