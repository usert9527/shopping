package com.user9527.shopping.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/10 15:51
 */
@Data
public class MergeVO {
    private Long purchaseId;
    private List<Long> items;
}
