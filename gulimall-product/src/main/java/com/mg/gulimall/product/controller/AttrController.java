package com.mg.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.mg.gulimall.product.vo.AttrRespVo;
import com.mg.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mg.gulimall.product.entity.AttrEntity;
import com.mg.gulimall.product.service.AttrService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * 商品属性
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 21:19:09
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 获取分类规格参数
     */
    @RequestMapping("/base/list/{catelogId}")
    public R attrCatelogList(@PathVariable("catelogId") Long catelogId, @RequestParam Map<String, Object> params) {
//        PageUtils page = attrService.queryPage(params);
        PageUtils attrList = attrService.findAttrList(catelogId, params);
        return R.ok().put("page", attrList);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
//        AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attr = attrService.getByInfoId(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrEntity attr) {
        attrService.updateById(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
