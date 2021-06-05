package com.user9527.shopping.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;
import com.user9527.shopping.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);
}

