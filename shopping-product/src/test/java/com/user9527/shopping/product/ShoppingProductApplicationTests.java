package com.user9527.shopping.product;

import com.user9527.shopping.product.entity.BrandEntity;
import com.user9527.shopping.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShoppingProductApplicationTests {

    @Autowired
    private BrandService brandService;


    @Test
    public void uploadTest() throws FileNotFoundException {
        int array[][] = new int[5][7];
        int index = 0;
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            for (int j = 0;  ; j++) {
                if(index == 0){
                    for (int k = 0; ; k++) {
                        int temp =  random.nextInt(35);
                        if(temp < 16){
                            array[i][index] = temp;
                     //       index ++;
                            return;
                        }else{
                            continue;
                        }
                    }
                }
                for (int k = 0; ; k++) {
                    int temp = random.nextInt(35);
                    if(index >= 7){
                        return;
                    }else{
                        for (int l = 0; ; l++) {
                            if(index >= 7){

                                return;
                            }
                            if(temp < array[i][index]){
                                array[i][++ index] = temp;
                            }else{
                                continue;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("123");
    }

    @Test
    public void contextLoads() {
        System.out.println("123");
    }

}
