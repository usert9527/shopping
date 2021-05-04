package com.user9527.shopping.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.R;
import com.user9527.shopping.product.entity.BrandEntity;
import com.user9527.shopping.product.entity.CategoryBrandRelationEntity;
import com.user9527.shopping.product.service.CategoryBrandRelationService;
import com.user9527.shopping.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     *  1 Controller，处理请求，接受和校验数据
     *  2 Service接受Controller传来的数据，进行业务处理
     *  3 Controller接受Service处理完的数据，封装页面指定的vo
     * @param catId
     * @return
     */
    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId",required = true) Long catId){
        List<BrandEntity> vos = categoryBrandRelationService.getBrandsByCatId(catId);

        List<BrandVO> collect = vos.stream().map(item -> {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(item.getBrandId());
            brandVO.setBrandName(item.getName());

            return brandVO;
        }).collect(Collectors.toList());

        return R.ok().put("data",collect);
    }

    /**
     * 获取当前品牌关联的所有分类列表
     * @param brandId
     * @return
     */
    @GetMapping("catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId)
        );

        return R.ok().put("data", data);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
