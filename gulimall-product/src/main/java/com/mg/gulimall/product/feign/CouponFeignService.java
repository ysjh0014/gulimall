package com.mg.gulimall.product.feign;

import com.mg.common.to.SkuReductionVo;
import com.mg.common.to.SpuBoundsVo;
import com.mg.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/spubounds/save")
    R saveBounds(@RequestBody SpuBoundsVo spuBoundsVo);

    @RequestMapping("/coupon/skufullreduction/saveInfo")
    R saveInfo(@RequestBody SkuReductionVo spuBoundsVo);
}
