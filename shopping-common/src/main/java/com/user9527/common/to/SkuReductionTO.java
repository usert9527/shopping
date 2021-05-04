package com.user9527.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/4 20:36
 */

@Data
public class SkuReductionTO {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
