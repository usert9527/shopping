package com.user9527.shopping.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/15 10:23
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
