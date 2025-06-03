package com.lab.rpc.common.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.lab.rpc.common.annotation.SPI;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    private static final Map<String, Cache> caches = new ConcurrentHashMap<>();

    /**
     * TODO
     * @param loadClass 要加载的类型
     * @return
     */
    public static synchronized <T> T load(Class<T> loadClass) {
        if (caches.containsKey(loadClass.getName())){
            return (T) caches.get(loadClass.getName()).getInstance("");
        }
        caches.put(loadClass.getName(), new Cache());
        Map<String, String> cache = caches.get(loadClass.getName()).cache;
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
                        cache.put(splits[0], splits[1]);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("加载SPI配置文件失败",e);
                }
            }
        }
        if(loadClass.isAnnotationPresent(SPI.class)){
            String value = loadClass.getAnnotation(SPI.class).value();
            if(cache.containsKey(value)){
                return (T)caches.get(loadClass.getName()).getInstance(value);
            }
        }
        return (T) caches.get(loadClass.getName()).getInstance("");
    }

    public static class Cache {
        /** 缓存
         * value: 全类名称
         */
        private final LinkedHashMap<String, String> cache = new LinkedHashMap<>();
        /**
         * 缓存实例, 多例无用
         */
        private Object instance = null;

        public Object getInstance(String name) {
            /**
             * if(instance != null){
             *    return instance;
             * }
             */
            if(cache.containsKey(name)){
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                try {
                    log.info("加载SPI实例:" + cache.get(name));
                    Class<?> class0 = classLoader.loadClass(cache.get(name));
                    instance = class0.newInstance();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("加载SPI实例失败",e);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return instance;
            }
            return getInstance(cache.keySet().iterator().next());
        }
    }
}
