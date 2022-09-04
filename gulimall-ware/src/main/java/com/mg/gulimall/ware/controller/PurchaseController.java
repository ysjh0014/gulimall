package com.mg.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mg.gulimall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mg.gulimall.ware.entity.PurchaseEntity;
import com.mg.gulimall.ware.service.PurchaseService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * 采购信息
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-22 20:45:08
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 领取采购单
     */
    @RequestMapping("/received")
    public R received(@RequestBody List<Long> items) {
        purchaseService.received(items);

        return R.ok();
    }



    /**
     * 合并采购需求
     */
    @RequestMapping("/merge")
    public R merge(@RequestBody PurchaseMergeVo purchaseMergeVo) {
        purchaseService.merge(purchaseMergeVo);

        return R.ok();
    }


    /**
     * 查询未领取的采购单
     */
    @RequestMapping("/unreceive/list")
    public R unreceive(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.unreceive(params);

        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
