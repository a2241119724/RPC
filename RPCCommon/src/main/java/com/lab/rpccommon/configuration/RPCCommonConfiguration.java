package com.lab.rpccommon.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.lab.rpccommon.handler.HeartResponseHandler;
import com.lab.rpccommon.handler.RPCDecoder;
import com.lab.rpccommon.handler.RPCEncoder;
import com.lab.rpccommon.spi.ISerializer;
import com.lab.rpccommon.utils.Utils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lab
 * @Title: RPCCommonConfiguration
 * @ProjectName RPC
 * @Description: 注册公共的Bean
 * @date 2025/4/21 0:22
 */
@Configuration
public class RPCCommonConfiguration {
    @Bean
    @ConditionalOnMissingBean(ISerializer.class)
    public ISerializer serializer(){
        return Utils.getInstanceBySPI(ISerializer.class);
    }

    @Bean
    public RPCEncoder rpcEncoder(){
        return new RPCEncoder();
    }

    @Bean
    public RPCDecoder rpcDecoder(){
        return new RPCDecoder();
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
