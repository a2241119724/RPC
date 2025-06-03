package com.lab.rpc.common.utils;

import com.lab.rpc.common.spi.SpiLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.ServiceLoader;

/**
 * @author lab
 * @title BeanUtils
 * @projectName RPC
 * @description 容器工具
 * @date 2025/4/14 23:22
 */
public class Utils implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ac) {
        context = ac;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static <T> T getInstanceBySpi(Class<T> clazz){
        /**
         * for (T t : ServiceLoader.load(clazz)) {
         *     return t;
         * }
         * return null;
         */
        return SpiLoader.load(clazz);
    }
}
