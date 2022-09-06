package com.mg.gulimall.search.controller;

import com.mg.common.exception.BizCodeEnume;
import com.mg.common.to.SkuModelVo;
import com.mg.common.utils.R;
import com.mg.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSearchController {

    @Autowired
    ProductSaveService productSaveService;

    @RequestMapping("/product")
    public R productStatusUp(@RequestBody List<SkuModelVo> skuModelVoList) {
        boolean status = false;
        try {
            status = productSaveService.productStatusUp(skuModelVoList);
        } catch (IOException e) {
            log.error("远程保存索引失败!");
        }
        if(status){
            return R.ok();
        } else {
             return R.error(BizCodeEnume.SEARCH_EXCEPTION.getCode(),BizCodeEnume.SEARCH_EXCEPTION.getMessage());
        }
    }

}
