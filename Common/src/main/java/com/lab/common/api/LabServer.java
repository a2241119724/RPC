package com.lab.common.api;

/**
 * @author lab
 * @title LabService
 * @projectName RPC
 * @description 服务接口
 * @date 2025/4/9 16:02
 */
public interface LabServer{
    /**
     * 获取信息
     * @return 信息
     */
    String getInfo();

    /**
     * 获取信息
     * @param str 信息
     * @return 信息
     */
    String getInfo(String str);
}
