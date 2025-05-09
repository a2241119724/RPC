package com.lab.consumer.controller;

import com.lab.common.api.LabServer;
import com.lab.rpcclient.annotation.RPCResource;
import org.springframework.web.bind.annotation.*;

/**
 * @author lab
 * @Title: RPCController
 * @ProjectName RPC
 * @Description: 测试接口
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
