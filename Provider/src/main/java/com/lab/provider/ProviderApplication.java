package com.lab.provider;

import com.lab.rpc.server.annotation.EnableRPCServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author LAB
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.lab.provider", "com.lab.common"})
@EnableRPCServer
public class ProviderApplication{
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
