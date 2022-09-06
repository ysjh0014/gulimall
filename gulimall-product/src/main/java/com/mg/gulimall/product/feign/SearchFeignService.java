package com.mg.gulimall.product.feign;

import com.mg.common.to.SkuModelVo;
import com.mg.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeignService {

    @RequestMapping("/search/save/product")
    public R productStatusUp(@RequestBody List<SkuModelVo> skuModelVoList);

}
