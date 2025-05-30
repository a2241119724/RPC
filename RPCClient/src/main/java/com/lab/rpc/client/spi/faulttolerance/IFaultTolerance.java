package com.lab.rpc.client.spi.faulttolerance;

import java.util.concurrent.Callable;

/**
 * @author lab
 * @title IFaultTolerance
 * @projectName RPC
 * @description 重试机制
 * @date 2025/4/21 0:11
 */
public interface IFaultTolerance {
    /**
     * 行重试
     * @param task 需要检测地任务
     * @return 响应数据
     * @param <T> 响应的类型
     */
    <T> T execute(Callable<T> task);

    /**
     * 计算延迟时间
     * @param attempt 尝试地次数
     * @return 延迟时间
     */
    float calculateDelay(int attempt);
}
