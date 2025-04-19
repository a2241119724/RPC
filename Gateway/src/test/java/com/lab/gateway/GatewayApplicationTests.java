package com.lab.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@SpringBootTest
class GatewayApplicationTests {

    @Test
    void contextLoads() throws IOException {
//        Selector selector = Selector.open();
//        selector.select()
    }

}
