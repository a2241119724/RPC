package com.lab.rpc.client.spi.faulttolerance;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lab
 * @title AbstractFaultTolerance
 * @projectName RPC
 * @description 重试机制的公共操作
 * @date 2025/4/21 1:17
 */
@Data
@Slf4j
@NoArgsConstructor
public abstract class AbstractFaultTolerance implements IFaultTolerance{
    protected int maxRetries = 3;
    protected float maxDelay = 4.0f;

    private CircuitState state = CircuitState.CLOSED;
    /** 触发熔断的失败次数阈值 */
    private int failureThreshold = 5;
    /** 熔断后自动恢复的时间窗口 */
    private long timeoutSeconds = 3;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(8, new DefaultThreadFactory("FaultTolerance-Scheduler"));

    public AbstractFaultTolerance(int failureThreshold, long timeoutSeconds) {
        this.failureThreshold = failureThreshold;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public <T> T execute(Callable<T> task){
        if (state == CircuitState.OPEN) {
            // 熔断中
            log.info("熔断:" + task.toString());
            return null;
        }
        int curRetries = 0;
        while (curRetries++ < maxRetries){
            try {
                T result = (T)task.call();
                success();
                return result;
            } catch (Throwable t) {
                float delay = calculateDelay(curRetries);
                try {
                    Thread.sleep((long)(delay * 1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            failure();
        }
        return null;
    }

    private void success() {
        if (state == CircuitState.HALF_OPEN) {
            // 半开状态下成功，关闭熔断器
            state = CircuitState.CLOSED;
            failureCount.set(0);
        }
        // 重置失败计数
        failureCount.set(0);
    }

    private void failure() {
        if (failureCount.incrementAndGet() >= failureThreshold) {
            state = CircuitState.OPEN;
            // 定时尝试恢复
            scheduler.schedule(()->{
                state = CircuitState.HALF_OPEN;
            }, timeoutSeconds, TimeUnit.SECONDS);
        }
        log.info("失败:" + failureCount.get());
    }

    private enum CircuitState {
        // 正常状态，请求直接执行
        CLOSED,
        // 熔断状态，直接拒绝请求
        OPEN,
        // 半开状态，允许试探性请求
        HALF_OPEN
    }
}
