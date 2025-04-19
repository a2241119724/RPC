package com.lab.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lab
 * @Title: MyController
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/17 23:36
 */
@RestController
@RequestMapping
public class MyController {
    @GetMapping
    public String test(){
        return "";
    }
}
