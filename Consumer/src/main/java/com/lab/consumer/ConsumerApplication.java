package com.lab.consumer;

import com.lab.rpc.client.annotation.EnableRPCClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author LAB
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.lab.consumer", "com.lab.common"})
@EnableRPCClient
public class ConsumerApplication{

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
