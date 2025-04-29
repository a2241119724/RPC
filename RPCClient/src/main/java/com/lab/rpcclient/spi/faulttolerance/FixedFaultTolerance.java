package com.lab.rpcclient.spi.faulttolerance;

/**
 * @author lab
 * @Title: FixedFaultTolerance
 * @ProjectName RPC
 * @Description: 固定时间回退
 * @date 2025/4/29 15:20
 */
public class FixedFaultTolerance extends AFaultTolerance {
    private float interval = 1.0f;

    public FixedFaultTolerance(float interval){
        this.interval = interval;
    }

    @Override
    public float calculateDelay(int attempt) {
        return interval;
    }
}
