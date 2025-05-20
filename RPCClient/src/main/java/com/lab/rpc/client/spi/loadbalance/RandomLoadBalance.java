package com.lab.rpc.client.spi.loadbalance;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lab
 * @title RandomLoadBalance
 * @projectName RPC
 * @description 随机负载均衡
 * @date 2025/4/20 18:38
 */
@Slf4j
public class RandomLoadBalance implements ILoadBalance{
    /** ThreadLocalRandom */
    private final Random random = new Random();

    @Override
    public <T> T select(List<T> addresses) {
        if(addresses.size() == 0){
            log.info("对应的服务没有提供者!");
            return null;
        }
        return addresses.get(random.nextInt(addresses.size()));
    }
}
