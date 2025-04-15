package com.lab.provider;

import com.lab.provider.netty.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

import javax.annotation.Resource;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lab.provider", "com.lab.common"})
@ConfigurationPropertiesScan(basePackages = {"com.lab.provider.property"})
public class ProviderApplication implements CommandLineRunner {
    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }
}
