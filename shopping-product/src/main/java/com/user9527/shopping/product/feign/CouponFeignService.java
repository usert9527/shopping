package com.user9527.shopping.product.feign;

import com.user9527.common.to.SkuReductionTO;
import com.user9527.common.to.SpuBoundTO;
import com.user9527.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/4 20:16
 */
@FeignClient("shopping-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);

}
