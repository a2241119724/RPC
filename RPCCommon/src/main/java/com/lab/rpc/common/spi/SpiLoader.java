package com.lab.rpc.common.spi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.List;
import java.util.Map;

/**
 * @author lab
 * @Title: SpiLoader
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/5/30 15:51
 */
public final class SpiLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/rpc/";

    private static final Log logger = LogFactory.getLog(SpiLoader.class);

    private static final Map<ClassLoader, Map<String, List<String>>> cache = new ConcurrentReferenceHashMap<>();
}
