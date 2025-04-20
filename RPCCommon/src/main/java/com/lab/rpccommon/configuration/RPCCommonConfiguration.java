package com.lab.rpccommon.configuration;

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
 * @Description: TODO
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
}
