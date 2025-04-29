package com.lab.rpcclient.spi.loadbalance;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author lab
 * @Title: PollingLoadBalance
 * @ProjectName RPC
 * @Description: 轮询负载均衡
 * @date 2025/4/20 18:42
 */
@Slf4j
public class PollingLoadBalance implements ILoadBalance{
    private static int index = 0;

    @Override
    public <T> T select(List<T> addresses) {
        if(addresses.size() == 0){
            log.info("对应的服务没有提供者!");
            return null;
        }
        return addresses.get(index++ % addresses.size());
    }
}
