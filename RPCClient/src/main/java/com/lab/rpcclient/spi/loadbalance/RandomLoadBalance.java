package com.lab.rpcclient.spi.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @author lab
 * @Title: RandomLoadBalence
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/20 18:38
 */
public class RandomLoadBalance implements ILoadBalance{
    private Random random = new Random();

    @Override
    public <T> T select(List<T> addresses) {
        return addresses.get(random.nextInt(addresses.size()));
    }
}
