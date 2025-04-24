package com.lab.rpcclient.spi.faulttolerance;

/**
 * @author lab
 * @Title: IFaultTolerance
 * @ProjectName RPC
 * @Description: 重试机制
 * @date 2025/4/21 0:11
 */
public interface IFaultTolerance {
    void execute(Runnable task);

    float calculateDelay(int attempt);
}
