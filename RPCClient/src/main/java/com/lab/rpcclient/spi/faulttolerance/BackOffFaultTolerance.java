package com.lab.rpcclient.spi.faulttolerance;

/**
 * @author lab
 * @Title: RetryFaultTolerance
 * @ProjectName RPC
 * @Description: 指数回退重试机制
 * @date 2025/4/21 0:12
 */
public class BackOffFaultTolerance extends AFaultTolerance{
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
