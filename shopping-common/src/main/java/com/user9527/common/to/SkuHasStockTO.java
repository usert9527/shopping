package com.user9527.common.to;

import lombok.Data;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/24 16:35
 */
@Data
public class SkuHasStockTO {
    private Long skuId;

    private Boolean hasStock;
}
