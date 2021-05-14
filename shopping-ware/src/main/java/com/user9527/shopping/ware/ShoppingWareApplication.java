package com.user9527.shopping.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.user9527.shopping.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingWareApplication.class, args);
    }

}
