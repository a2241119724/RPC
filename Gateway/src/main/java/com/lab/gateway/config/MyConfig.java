package com.lab.gateway.config;

import com.lab.gateway.interceptor.MyInterceptor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: MyConfig
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/17 23:32
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
    @Resource
    private MyInterceptor myInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
