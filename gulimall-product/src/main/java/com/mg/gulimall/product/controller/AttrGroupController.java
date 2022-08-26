package com.mg.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.mg.gulimall.product.entity.AttrEntity;
import com.mg.gulimall.product.service.AttrAttrgroupRelationService;
import com.mg.gulimall.product.service.AttrService;
import com.mg.gulimall.product.service.CategoryService;
import com.mg.gulimall.product.vo.AttrGroupRelationVo;
import com.mg.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.mg.gulimall.product.vo.AttrRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mg.gulimall.product.entity.AttrGroupEntity;
import com.mg.gulimall.product.service.AttrGroupService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * 属性分组
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 21:19:10
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;


    /**
     *获取属性分组的关联的所有属性
     * /product/attrgroup/{catelogId}/withattr
     */
    @GetMapping("/{catelogId}/withattr")
    public R withattrList(@PathVariable("catelogId") Long catelogId) {
        List<AttrGroupWithAttrsVo> page = attrGroupService.getWithAttr(catelogId);
        return R.ok().put("data", page);
    }


    /**
     *获取属性分组的关联的所有属性
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R relationList(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> page = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("page", page);
    }


    /**
     *获取属性分组没有关联的其他属性
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noRelationList(@PathVariable("attrgroupId") Long attrgroupId,@RequestParam Map<String,Object> params) {
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page", page);
    }


    /**
     *添加属性与分组关联关系
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVo) {
        relationService.saveBatchs(attrGroupRelationVo);
        return R.ok();
    }


    /**
     *删除属性与分组的关联关系
     */
    @PostMapping("/attr/relation/delete")
    public R delRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVo) {
        attrService.delRelation(attrGroupRelationVo);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
//        PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();

        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
