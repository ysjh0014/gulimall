package com.mg.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.mg.gulimall.product.vo.SpuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mg.gulimall.product.entity.SpuInfoEntity;
import com.mg.gulimall.product.service.SpuInfoService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * spu信息
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 21:19:09
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = spuInfoService.queryPageByParams(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
            SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuInfoVo spuInfoVo) {
//            spuInfoService.save(spuInfo);
        spuInfoService.saveBathInfo(spuInfoVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo) {
            spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
            spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
