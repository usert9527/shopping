package com.user9527.shopping.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.user9527.shopping.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingAuthServerApplication.class, args);
    }

}
