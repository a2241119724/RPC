package com.lab.rpc.client.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lab
 * @title RegisterCenterProperty
 * @projectName RPC
 * @description 注册中心的属性
 * @date 2025/4/18 22:32
 */
@Data
@ConfigurationProperties(prefix = "rpc.register.zookeeper")
public class RegisterCenterProperty {
    private String ip;
    private int port;

    public String getHost(){
        return ip + ":" + port;
    }
}
