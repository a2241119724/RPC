package com.lab.rpcserver.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lab
 * @Title: RegisterCenterProperty
 * @ProjectName RPC
 * @Description: 注册中心属性
 * @date 2025/4/18 22:32
 */
@Data
@ConfigurationProperties(prefix = "rpc.register.zookeeper")
public class ZKProperty {
    private String ip;
    private int port;

    public String getHost(){
        return ip + ":" + port;
    }
}
