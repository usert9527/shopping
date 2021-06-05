package com.user9527.shopping.product.feign;

import com.user9527.common.to.es.SkuEsModel;
import com.user9527.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/31 15:35
 */
@FeignClient("shopping-search")
public interface SearchFeignService {

    @PostMapping("/product")
    R saveProductAsIndices(@RequestBody List<SkuEsModel> skuEsModels);
}
