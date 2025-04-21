package com.lab.rpcclient.spi.faulttolerance;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author lab
 * @Title: RetryFaultTolerance
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/21 0:12
 */
public class RetryFaultTolerance extends AFaultTolerance{
    private float initialDelay = 0.5f;
    private float backoffFactor = 1.0f;

    @Override
    public void execute(Runnable task){
        int curRetries = 0;
        while (curRetries++ < maxRetries){
            try {
                task.run();
                break;
            } catch (Throwable t) {
                float delay = calculateDelay(curRetries);
                try {
                    Thread.sleep((long)(delay * 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public float calculateDelay(int attempt) {
        return (float) Math.min(initialDelay * Math.pow(backoffFactor, attempt - 1), maxDelay);
    }
}
