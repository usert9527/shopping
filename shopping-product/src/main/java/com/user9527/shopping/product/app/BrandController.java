package com.user9527.shopping.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.user9527.common.valid.AddGroup;
import com.user9527.common.valid.UpdateGroup;
import com.user9527.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.user9527.shopping.product.entity.BrandEntity;
import com.user9527.shopping.product.service.BrandService;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.R;


/**
 * 品牌
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated(value = AddGroup.class) @RequestBody BrandEntity brand/*, BindingResult result*/){
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(UpdateGroup.class)@RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated(UpdateStatusGroup.class)@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

    //search调用的远程服务
    @GetMapping("/infos")
    public R brandsInfo(@RequestParam("brandIds") List<Long> brandIds){
        List<BrandEntity> brands = brandService.getBrandsByIds(brandIds);

        return R.ok().put("brand", brands);
    }

}
