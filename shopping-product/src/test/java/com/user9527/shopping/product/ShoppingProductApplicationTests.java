package com.user9527.shopping.product;

import com.user9527.shopping.product.entity.BrandEntity;
import com.user9527.shopping.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("iphone");
        brandService.save(brandEntity);
    }

}
