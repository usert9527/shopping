package com.user9527.shopping.product.web;

import com.user9527.shopping.product.service.SkuInfoService;
import com.user9527.shopping.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/15 8:29
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId, Model model) throws ExecutionException, InterruptedException {
       SkuItemVo skuItemVo = skuInfoService.item(skuId);
       model.addAttribute("item", skuItemVo);
        return "item";
    }
}
