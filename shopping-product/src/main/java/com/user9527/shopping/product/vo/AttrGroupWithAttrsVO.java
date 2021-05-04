package com.user9527.shopping.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.user9527.shopping.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/4/29 17:20
 */

@Data
public class AttrGroupWithAttrsVO {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
