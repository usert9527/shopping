package com.user9527.shopping.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;

import com.user9527.shopping.ware.entity.WareSkuEntity;
import com.user9527.shopping.ware.vo.SkuHasStockVO;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:22:03
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVO> getSkuHasStock(List<Long> skuIds);
}

