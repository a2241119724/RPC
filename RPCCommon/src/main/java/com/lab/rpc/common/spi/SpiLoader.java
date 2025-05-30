package com.lab.rpc.common.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lab
 * @Title: SpiLoader
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/5/30 15:51
 */
@Slf4j
public final class SpiLoader {
    public static final List<String> SPI_LOCATION = new ArrayList<String>(){{
        add("META-INF/rpc/");
    }};

    private static final Map<String, Class<?>> cache = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> instances = new ConcurrentHashMap<>();

    /**
     * 只展示核心方法，完整内容可以去看源码
     * @param loadClass 要加载的类型
     * @return
     */
    public static Class<?> load(Class<?> loadClass) {
        log.info("加载类型为{}的SPI",loadClass.getName());
        for(String dir : SPI_LOCATION) {
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            for(URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] splits = line.split("=");
                        if(splits.length < 2) {
                            log.error("SPI配置文件格式错误");
                            continue;
                        }
                        String key = splits[0];
                        String className = splits[1];
                        // TODO
                        cache.put(loadClass.getName(), Class.forName(className));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("加载SPI配置文件失败",e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("需要加载的类不存在",e);
                }
            }
        }
        return cache.get(loadClass.getName());
    }

    public static <T> T getInstance(Class<?> tClass, String key) {
        Class<?> implClass = instances.get(key);
        String implClassName = implClass.getName();
        if (!cache.containsKey(implClassName)) {
            try {
                instances.put(implClassName, (Class<?>) implClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new RuntimeException(String.format("实例化 %s 失败", implClassName), e);
            }
        }
        return (T) cache.get(implClassName);
    }
}
