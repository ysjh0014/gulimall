package com.mg.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mg.gulimall.product.entity.BrandEntity;
import com.mg.gulimall.product.vo.CategoryBrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mg.gulimall.product.entity.CategoryBrandRelationEntity;
import com.mg.gulimall.product.service.CategoryBrandRelationService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 21:19:10
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 获取当前品牌的关联分类
     * /product/categorybrandrelation/brands/list
     */
    @GetMapping("/brands/list")
    public R categorybrandrelationList(@RequestParam("catId") Long catId) {
        List<BrandEntity> vos = categoryBrandRelationService.getCategoryBrandList(catId);
        List<CategoryBrandVo> collect = vos.stream().map(item -> {
            CategoryBrandVo brandVo = new CategoryBrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", collect);
    }


    /**
     * 获取当前品牌的关联分类
     */
    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId) {
        List<CategoryBrandRelationEntity> brandRelationEntities = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));

        return R.ok().put("data", brandRelationEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
            CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
            categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
            categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
