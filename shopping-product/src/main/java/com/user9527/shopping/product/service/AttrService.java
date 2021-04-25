package com.user9527.shopping.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;
import com.user9527.shopping.product.entity.AttrEntity;
import com.user9527.shopping.product.vo.AttrRespVO;
import com.user9527.shopping.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr( AttrVO attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVO getAttrInfo(Long attrId);

    void updateAttr(AttrVO attrVO);

    List<AttrEntity> getAttrRelation(Long attrGroupId);

    PageUtils getAttrNoRelation(Map<String, Object> params, Long attrgroupId);
}

