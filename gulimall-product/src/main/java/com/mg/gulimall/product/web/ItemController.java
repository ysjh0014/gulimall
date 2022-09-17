package com.mg.gulimall.product.web;

import com.mg.gulimall.product.service.SkuInfoService;
import com.mg.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @RequestMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        SkuItemVo skuItemVo=skuInfoService.item(skuId);
        model.addAttribute("item", skuItemVo);
        return "item";
    }

}
