package com.lab.rpcclient.annotation;

import com.lab.rpcclient.configuration.RPCClientAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lab
 * @Title: EnableRPCClient
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/19 13:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RPCClientAutoConfiguration.class})
@ConfigurationPropertiesScan(basePackages = {"com.lab.rpcclient.property"})
public @interface EnableRPCClient {
}
