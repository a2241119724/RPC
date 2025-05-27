package com.lab.consumer.controller;

import com.lab.common.api.LabServer;
import com.lab.rpc.client.annotation.RPCReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author lab
 * @title RpcController
 * @projectName RPC
 * @description 测试接口
 * @date 2025/4/9 15:58
 */
@RestController
@RequestMapping("/rpc")
public class RpcController {
    @RPCReference
    private LabServer labServer;

    @GetMapping("/{msg}")
    public String test(@PathVariable String msg){
        return labServer.getInfo(msg);
    }
}
