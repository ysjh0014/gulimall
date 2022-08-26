package com.mg.gulimall.product.service.impl;

import com.mg.common.utils.R;
import com.mg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.mg.gulimall.product.dao.AttrDao;
import com.mg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.mg.gulimall.product.entity.AttrEntity;
import com.mg.gulimall.product.service.AttrService;
import com.mg.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.AttrGroupDao;
import com.mg.gulimall.product.entity.AttrGroupEntity;
import com.mg.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao attrgroupRelationDao;
    @Autowired
    AttrDao attrDao;
    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            wrapper.and((obj)->{
               obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        if(catelogId == 0){
            IPage<AttrGroupEntity> entityIPage = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(entityIPage);
        } else {
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> entityIPage = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(entityIPage);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getWithAttr(Long catelogId) {
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrsVo> collect = groupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo withAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item,withAttrsVo);
            Long attrGroupId = item.getAttrGroupId();
            List<AttrEntity> relationAttr = attrService.getRelationAttr(attrGroupId);
            withAttrsVo.setAttrs(relationAttr);
            return withAttrsVo;
        }).collect(Collectors.toList());

        return collect;
    }

}