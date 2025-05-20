package com.lab.rpc.client.spi.faulttolerance;

/**
 * @author lab
 * @title FixedFaultTolerance
 * @projectName RPC
 * @description 固定时间回退
 * @date 2025/4/29 15:20
 */
public class FixedFaultTolerance extends AbstractFaultTolerance {
    private final float interval;

    public FixedFaultTolerance(float interval){
        this.interval = interval;
    }

    @Override
    public float calculateDelay(int attempt) {
        return interval;
    }
}
