package com.lab.rpcclient.spi.faulttolerance;

import lombok.NoArgsConstructor;

/**
 * @author lab
 * @Title: RetryFaultTolerance
 * @ProjectName RPC
 * @Description: 指数回退重试机制
 * @date 2025/4/21 0:12
 */
@NoArgsConstructor
public class BackOffFaultTolerance extends AFaultTolerance{
    private float initialDelay = 0.5f;
    private float backoffFactor = 1.0f;

    public BackOffFaultTolerance(float initialDelay, float backoffFactor){
        this.initialDelay = initialDelay;
        this.backoffFactor = backoffFactor;
    }

    public float calculateDelay(int attempt) {
        return (float) Math.min(initialDelay * Math.pow(backoffFactor, attempt - 1), maxDelay);
    }
}
