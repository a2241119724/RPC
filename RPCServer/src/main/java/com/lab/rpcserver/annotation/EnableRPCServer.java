package com.lab.rpcserver.annotation;

import com.lab.rpccommon.configuration.RPCCommonConfiguration;
import com.lab.rpcserver.configuration.RPCServerAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * @author lab
 * @Title: EnableRPC
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/18 17:45
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RPCServerAutoConfiguration.class, RPCCommonConfiguration.class})
@ConfigurationPropertiesScan(basePackages = {"com.lab.rpcserver.property"})
public @interface EnableRPCServer {
}
