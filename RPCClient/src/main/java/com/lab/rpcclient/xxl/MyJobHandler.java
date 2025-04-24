package com.lab.rpcclient.xxl;

import com.lab.rpcclient.netty.handler.NettyClientHandler;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lab
 * @Title: MyJobHandler
 * @ProjectName RPC
 * @Description: XxlJob相关
 * @date 2025/4/20 18:38
 */
public class MyJobHandler{
    @XxlJob(value = "MyJobHandler")
    public ReturnT<String> jobHandler(String param){
        System.out.println(NettyClientHandler.executor.getActiveCount());
        return ReturnT.SUCCESS;
    }
}

