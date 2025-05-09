package com.lab.rpcserver.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lab
 * @Title: NacosProperty
 * @ProjectName RPC
 * @Description: Nacos配置
 * @date 2025/4/29 15:08
 */
@Data
@ConfigurationProperties(prefix = "rpc.register.nacos")
public class NacosProperty {
    private String ip;
    private int port;
    private String username;
    private String password;

    public String getHost(){
        return ip + ":" + port;
    }
}
