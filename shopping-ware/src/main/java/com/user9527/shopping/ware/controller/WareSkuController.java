package com.user9527.shopping.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.user9527.shopping.ware.vo.SkuHasStockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.user9527.shopping.ware.entity.WareSkuEntity;
import com.user9527.shopping.ware.service.WareSkuService;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.R;



/**
 * 商品库存
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:22:03
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> SkuIds){
        List<SkuHasStockVO> vos = wareSkuService.getSkuHasStock(SkuIds);
        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
