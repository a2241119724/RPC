package com.lab.rpcclient.spi.loadbalance;

import java.util.List;

/**
 * @author lab
 * @Title: ILoadBalance
 * @ProjectName RPC
 * @Description: 负载均衡
 * @date 2025/4/20 18:31
 */
public interface ILoadBalance {
    <T> T select(List<T> addresses);
}
