package com.lab.rpcserver.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * @author lab
 * @Title: RPCServer
 * @ProjectName RPC
 * @Description: 可以被远程调用
 * @date 2025/4/9 20:11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
public @interface RPCServer {
}
