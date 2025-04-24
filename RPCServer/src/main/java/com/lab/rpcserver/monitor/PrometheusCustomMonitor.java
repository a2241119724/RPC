package com.lab.rpcserver.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import javax.annotation.PostConstruct;

/**
 * @author lab
 * @Title: PrometheusCustomMonitor
 * @ProjectName RPC
 * @Description: 可视化相关数据
 * @date 2025/4/18 22:09
 */
public class PrometheusCustomMonitor {
    @Getter
    private Counter reportDialRequestCount;
    private String callReportUrl = "lab";
    @Getter
    private Timer reportDialResponseTime;
    @Getter
    private final MeterRegistry registry;

    public PrometheusCustomMonitor(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        reportDialRequestCount = registry.counter("go_api_report_dial_request_count", "url",callReportUrl);
        reportDialResponseTime = registry.timer("go_api_report_dial_response_time", "url",callReportUrl);
    }
}

