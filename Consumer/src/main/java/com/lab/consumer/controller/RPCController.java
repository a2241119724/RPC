package com.lab.consumer.controller;

import com.lab.common.api.LabServer;
import com.lab.consumer.annotation.RPCResource;
import com.lab.consumer.netty.handler.NettyClientHandler;
import org.springframework.web.bind.annotation.*;

/**
 * @author lab
 * @Title: RPCController
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/9 15:58
 */
@RestController
@RequestMapping("/rpc")
public class RPCController {
    @RPCResource
    private LabServer labServer;

    @GetMapping("/{msg}")
    public String test(@PathVariable String msg){
        return labServer.getInfo(msg);
    }
}
