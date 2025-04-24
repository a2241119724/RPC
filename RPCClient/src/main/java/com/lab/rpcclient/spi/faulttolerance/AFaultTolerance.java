package com.lab.rpcclient.spi.faulttolerance;

import lombok.Data;

/**
 * @author lab
 * @Title: AFaultTolerance
 * @ProjectName RPC
 * @Description: 重试机制的公共操作
 * @date 2025/4/21 1:17
 */
@Data
public abstract class AFaultTolerance implements IFaultTolerance{
    protected int maxRetries = 3;
    protected float maxDelay = 4.0f;
}
