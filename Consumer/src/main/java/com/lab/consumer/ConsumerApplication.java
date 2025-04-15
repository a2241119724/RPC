package com.lab.consumer;

import com.lab.consumer.netty.NettyClient;
import com.lab.consumer.zookeeper.ServerDiscovery;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lab.consumer", "com.lab.common"})
public class ConsumerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Override
    public void run(String... args){
        ServerDiscovery.getInstance().connect();
    }
}
