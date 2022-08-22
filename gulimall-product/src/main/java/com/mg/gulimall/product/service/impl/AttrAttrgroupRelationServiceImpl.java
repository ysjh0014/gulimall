package com.mg.gulimall.product.service.impl;

import com.mg.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.mg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.mg.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatchs(List<AttrGroupRelationVo> attrGroupRelationVo) {
        List<AttrAttrgroupRelationEntity> collect = attrGroupRelationVo.stream().map(relation -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(relation, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}