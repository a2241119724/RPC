package com.lab.rpccommon.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ServiceLoader;

/**
 * @author lab
 * @Title: BeanUtils
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/14 23:22
 */
public class Utils implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        context = ac;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static <T> T getInstanceBySPI(Class<T> clazz){
        for (T t : ServiceLoader.load(clazz)) {
            return t;
        }
        return null;
    }
}
