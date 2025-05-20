package com.lab.rpc.client.annotation;

import com.lab.rpc.client.configuration.RpcClientAutoConfiguration;
import com.lab.rpc.common.configuration.RpcCommonConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lab
 * @title EnableRPCClient
 * @projectName RPC
 * @description 注入第三方包(这个包)的相关Bean
 * @date 2025/4/19 13:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RpcClientAutoConfiguration.class, RpcCommonConfiguration.class})
@ConfigurationPropertiesScan(basePackages = {"com.lab.rpc.client.property"})
public @interface EnableRPCClient {
}
