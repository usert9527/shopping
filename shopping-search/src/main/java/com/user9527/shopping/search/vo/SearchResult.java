package com.user9527.shopping.search.vo;

import com.user9527.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/28 20:18
 */
@Data
public class SearchResult {
    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 当前页面
     */
    private Integer pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页面
     */
    private Integer totalPages;
    /**
     * 品牌
     */
    private List<BrandVO> brands;
    /**
     * 分类
     */
    private List<CatalogVO> catalogs;
    /**
     * 所有属性
     */
    private List<AttrVO> attrs;

    private List<Integer> pageNavs;

    /**
     * 面包屑导航数据
     */
    private List<NavVo> navs = new ArrayList<>();

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVO{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVO{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVO{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
    private List<Long> attrIds = new ArrayList<>();
}
