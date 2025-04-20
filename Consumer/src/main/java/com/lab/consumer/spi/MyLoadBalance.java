package com.lab.consumer.spi;

import com.lab.rpcclient.spi.loadbalance.ILoadBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @author lab
 * @Title: MyLoadBalance
 * @ProjectName RPC
 * @Description: TODO
 * @date 2025/4/20 23:41
 */
@Controller
public class MyLoadBalance implements ILoadBalance {
    @Override
    public <T> T select(List<T> addresses) {
        return addresses.get(0);
    }
}
