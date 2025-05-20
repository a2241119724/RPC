package com.lab.rpc.server.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author lab
 * @title NettyServerProperty
 * @projectName RPC
 * @description Netty服务属性
 * @date 2025/4/11 21:21
 */
@Data
@ConfigurationProperties(prefix="rpc.netty.server")
public class NettyServerProperty {
    private String host;
    private int port;

    public NettyServerProperty(){
        try {
            this.host = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
