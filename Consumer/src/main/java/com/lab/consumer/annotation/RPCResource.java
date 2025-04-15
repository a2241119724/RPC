package com.lab.consumer.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * @author lab
 * @Title: RPCResource
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 20:46
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RPCResource
{
}
