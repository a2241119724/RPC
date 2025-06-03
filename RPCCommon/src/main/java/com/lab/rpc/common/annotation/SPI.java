package com.lab.rpc.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lab
 * @Title: SPI
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/6/3 21:22
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SPI {
    String value() default "";
}
