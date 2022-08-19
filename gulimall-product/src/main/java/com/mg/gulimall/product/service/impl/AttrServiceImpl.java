package com.mg.gulimall.product.service.impl;

import com.mg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.mg.gulimall.product.dao.AttrGroupDao;
import com.mg.gulimall.product.dao.CategoryDao;
import com.mg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.mg.gulimall.product.entity.AttrGroupEntity;
import com.mg.gulimall.product.entity.CategoryEntity;
import com.mg.gulimall.product.service.AttrAttrgroupRelationService;
import com.mg.gulimall.product.service.AttrGroupService;
import com.mg.gulimall.product.service.CategoryService;
import com.mg.gulimall.product.vo.AttrRespVo;
import com.mg.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.AttrDao;
import com.mg.gulimall.product.entity.AttrEntity;
import com.mg.gulimall.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao attrgroupRelationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils findAttrList(Long catelogId, Map<String, Object> params) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("attr_id", key).or().like("attr_name", key);
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        List<AttrEntity> attrEntities = page.getRecords();
        PageUtils pageUtils = new PageUtils(page);
        List<AttrRespVo> attrRespVos = attrEntities.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            AttrAttrgroupRelationEntity relationEntity = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //保存关联关系
        if (attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrgroupRelationDao.insert(relationEntity);
        }
    }

    @Override
    public AttrRespVo getByInfoId(Long attrId) {
        AttrEntity entity = this.getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(entity, attrRespVo);
        //设置分组信息
        AttrAttrgroupRelationEntity relationEntity = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if(relationEntity!=null){
            attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
            AttrGroupEntity groupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
            if(groupEntity!=null){
                attrRespVo.setGroupName(groupEntity.getAttrGroupName());
            }
        }


        //设置分类信息
        Long[] catelogPath = categoryService.findCatelogPath(entity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);

        return attrRespVo;
    }

}