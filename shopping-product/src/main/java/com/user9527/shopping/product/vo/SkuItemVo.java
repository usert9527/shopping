package com.user9527.shopping.product.vo;

import com.user9527.shopping.product.entity.SkuImagesEntity;
import com.user9527.shopping.product.entity.SkuInfoEntity;
import com.user9527.shopping.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/15 8:39
 */
@Data
public class SkuItemVo {
    /**
     * sku基本信息获取 pms_sku_info
     */
    private SkuInfoEntity info;

    /**
     *sku图片信息 pms_sku_images
     */
    private List<SkuImagesEntity> images;

    /**
     * spu的销售属性组合
     */
    private List<SkuItemSaleAttrVo> saleAttr;

    /**
     * spu的介绍
     */
    private SpuInfoDescEntity desc;

    /**
     * spu的规格参数信息
     */
    private List<SpuItemAttrGroupVo> groupAttrs;

    private boolean hasStock = true;
}
