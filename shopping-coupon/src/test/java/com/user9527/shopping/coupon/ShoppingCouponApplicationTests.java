package com.user9527.shopping.coupon;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class ShoppingCouponApplicationTests {

    @Test
    void contextLoads() {
        Random random = new Random();

        System.out.println(random.nextInt());
    }


}
