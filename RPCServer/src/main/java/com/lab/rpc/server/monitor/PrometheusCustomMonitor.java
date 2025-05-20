package com.lab.rpc.server.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import javax.annotation.PostConstruct;

/**
 * @author lab
 * @title PrometheusCustomMonitor
 * @projectName RPC
 * @description 可视化相关数据
 * @date 2025/4/18 22:09
 */
public class PrometheusCustomMonitor {
    @Getter
    private Counter reportDialRequestCount;
    private static final String CALL_REPORT_URL = "lab";
    @Getter
    private Timer reportDialResponseTime;
    @Getter
    private final MeterRegistry registry;

    public PrometheusCustomMonitor(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        reportDialRequestCount = registry.counter("go_api_report_dial_request_count", "url", CALL_REPORT_URL);
        reportDialResponseTime = registry.timer("go_api_report_dial_response_time", "url", CALL_REPORT_URL);
    }
}

