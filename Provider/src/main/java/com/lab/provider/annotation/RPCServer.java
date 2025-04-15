package com.lab.provider.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * @author lab
 * @Title: RPCServer
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 20:11
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Controller
public @interface RPCServer {
}
