package com.mg.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mg.common.constant.ProductConstant;
import com.mg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.mg.gulimall.product.dao.AttrGroupDao;
import com.mg.gulimall.product.dao.CategoryDao;
import com.mg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.mg.gulimall.product.entity.AttrGroupEntity;
import com.mg.gulimall.product.entity.CategoryEntity;
import com.mg.gulimall.product.service.AttrAttrgroupRelationService;
import com.mg.gulimall.product.service.AttrGroupService;
import com.mg.gulimall.product.service.CategoryService;
import com.mg.gulimall.product.vo.AttrGroupRelationVo;
import com.mg.gulimall.product.vo.AttrRespVo;
import com.mg.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
    @Autowired
    AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils findAttrList(Long catelogId, Map<String, Object> params, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type","base".equals(attrType)? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
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

            if("base".equals(attrType)) {
                AttrAttrgroupRelationEntity relationEntity = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
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
        if (attr.getAttrGroupId() != null && attr.getAttrGroupId()!=null) {
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

    @Override
    public void updateAttrById(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        //更新分组信息
        AttrAttrgroupRelationEntity attrAttrgroupRelation = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelation.setAttrId(attr.getAttrId());
        attrAttrgroupRelation.setAttrGroupId(attr.getAttrGroupId());

        Integer count = attrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        if(count>0){
            attrgroupRelationDao.update(attrAttrgroupRelation,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
        } else {
            attrgroupRelationDao.insert(attrAttrgroupRelation);
        }

    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        QueryWrapper<AttrAttrgroupRelationEntity> attrGroupRelation = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId);
        List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(attrGroupRelation);
        List<Long> collects = relationEntities.stream().map((relation) -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());
        if(collects==null||collects.size()==0){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(collects);
        return (List<AttrEntity>)attrEntities;
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //查询分组的明细信息
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        //当前分组只能关联其他分组没有关联的属性
        //当前分组下的其他属性
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = attrGroupEntities.stream().map(group -> {
            return group.getAttrGroupId();
        }).collect(Collectors.toList());

        //这些分组关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = attrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> longList = relationEntities.stream().map((relation) -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());

        //从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(longList!=null&&longList.size()!=0){
            queryWrapper.notIn("attr_id",longList);
        }
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().eq("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public void delRelation(List<AttrGroupRelationVo> attrGroupRelationVo) {
        List<AttrAttrgroupRelationEntity> collects = attrGroupRelationVo.stream().map(relation -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(relation, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrgroupRelationDao.deleteBatchRelation(collects);
    }

}