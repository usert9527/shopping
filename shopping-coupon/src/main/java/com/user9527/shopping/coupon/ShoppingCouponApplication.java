package com.user9527.shopping.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication()
public class ShoppingCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCouponApplication.class, args);
    }

}
