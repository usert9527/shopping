package com.user9527.shopping.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/28 16:43
 */
@Data
public class SearchParam {
    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;
    /**
     * 三级分类id
     */
    private Long catalog3Id;
    /**
     * 排序条件
     * 销量 sort=saleCount_acs/desc
     * 价格      skuPrice_acs/desc
     * 综合排序  hotScore_acs/desc
     */
    private String sort;
    /**
     * 是否只显示有货
     */
    private Integer hasStock;
    /**
     * 价格区间查询
     */
    private String skuPrice;
    /**
     * 按照品牌查询
     */
    private List<Long> brandId;
    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;
    /**
     * 页面
     */
    private Integer pageNum = 1;

    /**
     * 原生所有的查询条件
     */
    private String _queryString;
}
