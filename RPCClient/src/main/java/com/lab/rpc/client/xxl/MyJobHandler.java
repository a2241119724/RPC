package com.lab.rpc.client.xxl;

import com.lab.rpc.client.netty.handler.NettyClientHandler;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;

/**
 * @author lab
 * @title MyJobHandler
 * @projectName RPC
 * @description XxlJob相关
 * @date 2025/4/20 18:38
 */
public class MyJobHandler{
    @XxlJob(value = "MyJobHandler")
    public ReturnT<String> jobHandler(String param){
        System.out.println(NettyClientHandler.executor.getActiveCount());
        return ReturnT.SUCCESS;
    }
}

