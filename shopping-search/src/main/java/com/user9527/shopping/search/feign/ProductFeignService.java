package com.user9527.shopping.search.feign;

import com.user9527.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/8 20:49
 */
@FeignClient("shopping-product")
public interface  ProductFeignService {
    @GetMapping("product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("product/brand/infos")
    R brandsInfo(@RequestParam("brandIds") List<Long> brandIds);
}
