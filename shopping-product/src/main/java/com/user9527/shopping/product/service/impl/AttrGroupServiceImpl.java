package com.user9527.shopping.product.service.impl;

import com.user9527.shopping.product.entity.AttrEntity;
import com.user9527.shopping.product.service.AttrService;
import com.user9527.shopping.product.vo.AttrGroupWithAttrsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.Query;

import com.user9527.shopping.product.dao.AttrGroupDao;
import com.user9527.shopping.product.entity.AttrGroupEntity;
import com.user9527.shopping.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            wrapper.and((obj) ->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        if( catelogId == 0 ){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }else {
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 查询所有分组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        List<AttrGroupWithAttrsVO> collect = groupEntities.stream().map(groupEntitie -> {

            AttrGroupWithAttrsVO attrGroupWithAttrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(groupEntitie, attrGroupWithAttrsVO);

            List<AttrEntity> attrEntities = attrService.getAttrRelation(attrGroupWithAttrsVO.getAttrGroupId());
            attrGroupWithAttrsVO.setAttrs(attrEntities);

            return attrGroupWithAttrsVO;
        }).collect(Collectors.toList());

        return collect;

    }

}