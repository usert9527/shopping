package com.user9527.shopping.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.user9527.shopping.product.entity.AttrEntity;
import com.user9527.shopping.product.service.AttrAttrgroupRelationService;
import com.user9527.shopping.product.service.AttrService;
import com.user9527.shopping.product.service.CategoryService;
import com.user9527.shopping.product.vo.AttrAttrgroupRelationVO;
import com.user9527.shopping.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.user9527.shopping.product.entity.AttrGroupEntity;
import com.user9527.shopping.product.service.AttrGroupService;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.R;



/**
 * 属性分组
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;



    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        // 1 查出当前分类下的所有分组
        // 2 查出每个分组下的所有属性
        List<AttrGroupWithAttrsVO> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }

    @PostMapping("/attr/relation")
    public R addAttrRelation(@RequestBody List<AttrAttrgroupRelationVO> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }

    /**
     * 获取属性分组没有关联的其他属性
     * @param attrgroupId
     * @param params
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params){

        PageUtils page = attrService.getAttrNoRelation(params, attrgroupId);
        return R.ok().put("page", page);
    }

    /**
     * 删除属性与分组的关联关系
     * @param relationVO
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrAttrgroupRelationVO[] relationVO){
        relationService.deleteAttrRelation(relationVO);
        return R.ok();
    }

    /**
     * 获取属性分组的关联的所有属性
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getAttrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrEntities = attrService.getAttrRelation(attrgroupId);

        return R.ok().put("data",attrEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
