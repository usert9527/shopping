package com.user9527.shopping.search.controller;

import com.user9527.common.exception.BizCodeEnum;
import com.user9527.common.to.es.SkuEsModel;
import com.user9527.common.utils.R;
import com.user9527.shopping.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/31 15:38
 */
@Slf4j
@RestController
public class ElasticSaveController {
    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R saveProductAsIndices(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean  b = false;
        try {
            b = productSaveService.saveProductAsIndices(skuEsModels);
        } catch (Exception e) {
            log.error("远程保存索引失败");
        }
        if (!b){
            return R.ok();
        }else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

    }
}
