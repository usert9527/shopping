package com.user9527.shopping.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;
import com.user9527.shopping.product.entity.AttrGroupEntity;
import com.user9527.shopping.product.vo.AttrGroupWithAttrsVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

