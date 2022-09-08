package com.mg.gulimall.product.service.impl;

import com.mg.gulimall.product.service.CategoryBrandRelationService;
import com.mg.gulimall.product.vo.CateLog2Vo;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.CategoryDao;
import com.mg.gulimall.product.entity.CategoryEntity;
import com.mg.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        List<CategoryEntity> levelMenus = categoryEntities.stream().filter(categoryEntity ->
             categoryEntity.getParentCid() == 0
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity,categoryEntities));
            return categoryEntity;
        }).sorted((menu1,menu2) -> {
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());


        return levelMenus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前要删除的菜单是否被其他地方所引用

        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList();
        List<Long> catelogPath = findParentCatelogPath(catelogId,paths);
        Collections.reverse(catelogPath);
        return catelogPath.toArray(new Long[catelogPath.size()]);
    }

    @Override
    public void updateCategoryDetail(CategoryEntity category) {
        this.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevelOne() {
//        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", "0"));
//        System.out.println("消耗时间："+(System.currentTimeMillis()-l));
        return categoryEntities;
    }

    @Override
    public Map<String, List<CateLog2Vo>> getCatalogJson() {
        //查出所有分类
        List<CategoryEntity> levelOne = getLevelOne();
        Map<String, List<CateLog2Vo>> parent_cid = levelOne.stream().collect(Collectors.toMap(item -> item.getCatId().toString(), v -> {
            //查询每一个一级分类的二级分类
            List<CategoryEntity> entityList = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            //封装二级分类
            List<CateLog2Vo> cateLog2Vos = null;
            if (entityList != null&&entityList.size()!=0) {
                cateLog2Vos = entityList.stream().map(level2 -> {
                    CateLog2Vo cateLog2Vo = new CateLog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //查询当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", level2.getCatId()));
                    if(entities!=null&&entities.size()!=0){
                        List<CateLog2Vo.CateLog3Vo> cateLog3Vos = entities.stream().map(leve3 -> {
                            CateLog2Vo.CateLog3Vo cateLog3Vo = new CateLog2Vo.CateLog3Vo(leve3.getCatId().toString(), level2.getCatId().toString(), leve3.getName());
                            return cateLog3Vo;
                        }).collect(Collectors.toList());
                        cateLog2Vo.setCatalog3List(cateLog3Vos);
                    }

                    return cateLog2Vo;
                }).collect(Collectors.toList());
            }
            return cateLog2Vos;
        }));
        return parent_cid;
    }

    private List<Long> findParentCatelogPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        if(categoryEntity.getParentCid()!=0){
            findParentCatelogPath(categoryEntity.getParentCid(),paths);
        }
        return paths;
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){

        List<CategoryEntity> categoryEntityList = all.stream().filter((categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        })).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return categoryEntityList;
    }
}