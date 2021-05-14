package com.user9527.shopping.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseDoneVO {

    @NotNull
    private Long id;

    private List<PurchaseItemDoneVO> items;
}
