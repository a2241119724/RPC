package com.lab.rpc.server.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * @author lab
 * @title RPCServer
 * @projectName RPC
 * @description 可以被远程调用
 * @date 2025/4/9 20:11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
public @interface RPCServer {
}
