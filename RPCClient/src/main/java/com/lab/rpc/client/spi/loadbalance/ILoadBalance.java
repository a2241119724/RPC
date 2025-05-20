package com.lab.rpc.client.spi.loadbalance;

import java.util.List;

/**
 * @author lab
 * @title ILoadBalance
 * @projectName RPC
 * @description 负载均衡
 * @date 2025/4/20 18:31
 */
public interface ILoadBalance {
    /**
     * 负载均衡
     * @param addresses 服务列表
     * @return 选择地服务
     * @param <T> 服务地址类型
     */
    <T> T select(List<T> addresses);
}
