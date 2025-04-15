package com.lab.consumer.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author lab
 * @Title: BeanUtils
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/14 23:22
 */
@Component
public class BeanUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        context = ac;
    }

    // 根据类型获取Bean
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    // 根据名称和类型获取Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}
