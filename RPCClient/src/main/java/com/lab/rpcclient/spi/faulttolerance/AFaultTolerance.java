package com.lab.rpcclient.spi.faulttolerance;

import lombok.Data;

/**
 * @author lab
 * @Title: AFaultTolerance
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/21 1:17
 */
@Data
public abstract class AFaultTolerance implements IFaultTolerance{
    protected int maxRetries = 3;
    protected float maxDelay = 4.0f;
}
