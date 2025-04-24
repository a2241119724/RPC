package com.lab.rpcclient.spi.loadbalance;

import java.util.List;

/**
 * @author lab
 * @Title: PollingLoadBalance
 * @ProjectName RPC
 * @Description: 轮询负载均衡
 * @date 2025/4/20 18:42
 */
public class PollingLoadBalance implements ILoadBalance{
    private static int index = 0;

    @Override
    public <T> T select(List<T> addresses) {
        return addresses.get(index++ % addresses.size());
    }
}
