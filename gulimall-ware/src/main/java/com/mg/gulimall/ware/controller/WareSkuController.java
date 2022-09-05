package com.mg.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mg.common.to.SkuStacksVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mg.gulimall.ware.entity.WareSkuEntity;
import com.mg.gulimall.ware.service.WareSkuService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * 商品库存
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-22 20:45:08
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    /**
     * 列表
     */
    @RequestMapping("/getSkuStocks")
    public R getSkuStocks(@RequestBody List<Long> ids) {
        List<SkuStacksVo> skuStocks = wareSkuService.getSkuStocks(ids);
        return R.ok().setData(skuStocks);
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
            WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
            wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
            wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
            wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
