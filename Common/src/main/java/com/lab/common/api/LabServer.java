package com.lab.common.api;

/**
 * @author lab
 * @Title: LabService
 * @ProjectName RPC
 * @Description: 服务接口
 * @date 2025/4/9 16:02
 */
public interface LabServer{
    String getInfo();

    String getInfo(String str);
}
