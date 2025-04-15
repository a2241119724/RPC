package com.lab.provider.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PrometheusCustomMonitor {
    @Getter
    private Counter reportDialRequestCount;
    private String callReportUrl = "111";
    @Getter
    private Timer reportDialResponseTime;
    @Getter
    private final MeterRegistry registry;

    @Autowired
    public PrometheusCustomMonitor(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        reportDialRequestCount = registry.counter("go_api_report_dial_request_count", "url",callReportUrl);
        reportDialResponseTime = registry.timer("go_api_report_dial_response_time", "url",callReportUrl);
    }
}

