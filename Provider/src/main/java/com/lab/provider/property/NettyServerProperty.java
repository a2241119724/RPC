package com.lab.provider.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author lab
 * @Title: NettyServerProperty
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/11 21:21
 */
@Data
@Controller
@ConfigurationProperties(prefix="rpc")
public class NettyServerProperty {
    private String host;
    private int port;

    public NettyServerProperty() throws UnknownHostException {
        this.host = Inet4Address.getLocalHost().getHostAddress();
    }
}
