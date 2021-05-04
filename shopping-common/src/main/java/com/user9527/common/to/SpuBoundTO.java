package com.user9527.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/4 20:29
 */

@Data
public class SpuBoundTO {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
