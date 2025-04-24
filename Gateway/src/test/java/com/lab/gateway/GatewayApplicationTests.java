package com.lab.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;

@SpringBootTest
class GatewayApplicationTests {

    @Test
    void contextLoads() throws IOException, ClassNotFoundException {
        new Children();
    }

}

class Children{
    static int childrenRes = 1;

    static {
        childrenRes = 2;
        System.out.println(1);
    }

    int childrenRes1 = 1;

    {
        childrenRes1 = 2;
        System.out.println(3);
    }

    public Children(){
        System.out.println(2);
    }

}
