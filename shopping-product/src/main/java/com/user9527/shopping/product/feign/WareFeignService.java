package com.user9527.shopping.product.feign;

import com.user9527.common.to.SkuHasStockTO;
import com.user9527.common.utils.R;
import com.user9527.common.vo.SkuHasStockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/24 17:17
 */

@FeignClient("shopping-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> SkuIds);
}
