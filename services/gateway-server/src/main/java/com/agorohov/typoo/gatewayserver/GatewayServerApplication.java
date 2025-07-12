package com.agorohov.typoo.gatewayserver;

import com.agorohov.shared.utils.dotenvloader.EnableDotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDotenvLoader
//@EnableDiscoveryClient        // TODO это надо?!
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
