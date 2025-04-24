package com.lab.rpcclient.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * @author lab
 * @Title: RPCResource
 * @ProjectName RPC
 * @Description: 在调用对应的函数时，使用代理调用服务端服务
 * @date 2025/4/9 20:46
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RPCResource
{
}
