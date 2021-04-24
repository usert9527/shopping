package com.user9527.shopping.product.vo;

import lombok.Data;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/4/14 15:54
 */
@Data
public class AttrRespVO extends AttrVO{

    /**
     * 分类名
     */
    private String catelogName;

    /**
     * 分组名
     */
    private String groupName;
}
