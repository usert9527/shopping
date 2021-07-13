package com.user9527.shopping.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/6 11:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2VO {
    private String catalog1Id;
    private String id;
    private String name;

    List<Catelog3VO> catalog3List;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3VO{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
