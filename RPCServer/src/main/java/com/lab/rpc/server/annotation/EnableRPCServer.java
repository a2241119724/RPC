package com.lab.rpc.server.annotation;

import com.lab.rpc.server.configuration.RpcServerAutoConfiguration;
import com.lab.rpc.common.configuration.RpcCommonConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * @author lab
 * @title EnableRPC
 * @projectName RPC
 * @description 配置第三方包(这个包)的Bean
 * @date 2025/4/18 17:45
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RpcServerAutoConfiguration.class, RpcCommonConfiguration.class})
@ConfigurationPropertiesScan(basePackages = {"com.lab.rpc.server.property"})
public @interface EnableRPCServer {
}
