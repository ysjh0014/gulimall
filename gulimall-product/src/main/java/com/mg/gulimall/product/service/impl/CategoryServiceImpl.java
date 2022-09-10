package com.mg.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mg.gulimall.product.service.CategoryBrandRelationService;
import com.mg.gulimall.product.vo.CateLog2Vo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    @Autowired
    StringRedisTemplate redisTemplate;

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
            categoryEntity.setChildren(getChildren(categoryEntity, categoryEntities));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
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
        List<Long> catelogPath = findParentCatelogPath(catelogId, paths);
        Collections.reverse(catelogPath);
        return catelogPath.toArray(new Long[catelogPath.size()]);
    }

    @Override
    public void updateCategoryDetail(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
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
        //将缓存中放json字符串，拿出的json字符串，还需你转为能用的对象类型[序列化与反序列化]

        //1.加入缓存逻辑，缓存中存的数据是json字符串
        //JSON跨语言、跨平台兼容
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String catelogJson = ops.get("catelogJson");
        if(StringUtils.isEmpty(catelogJson)){
            //缓存中没有，查数据库
            Map<String, List<CateLog2Vo>> catalogJsonFordb = getCatalogJsonFordb();
            //查到的数据再放入缓存中，将对象转为json放到缓存中
            String s = JSON.toJSONString(catalogJsonFordb);
            ops.set("catelogJson",s);
            return catalogJsonFordb;
        }
        //转为指定的对象
        Map<String, List<CateLog2Vo>> stringListMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<CateLog2Vo>>>() {
        });
        return stringListMap;
    }




    public Map<String, List<CateLog2Vo>> getCatalogJsonFordb() {
        //将数据库的多次查询变为一次查询
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //查出所有分类
        List<CategoryEntity> levelOne = getParent_cid(categoryEntities,0L);
        Map<String, List<CateLog2Vo>> parent_cid = levelOne.stream().collect(Collectors.toMap(item -> item.getCatId().toString(), v -> {
            //查询每一个一级分类的二级分类
            List<CategoryEntity> entityList = getParent_cid(categoryEntities,v.getCatId());
            //封装二级分类
            List<CateLog2Vo> cateLog2Vos = null;
            if (entityList != null && entityList.size() != 0) {
                cateLog2Vos = entityList.stream().map(level2 -> {
                    CateLog2Vo cateLog2Vo = new CateLog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //查询当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> entities = getParent_cid(categoryEntities,level2.getCatId());
                    if (entities != null && entities.size() != 0) {
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
        if (categoryEntity.getParentCid() != 0) {
            findParentCatelogPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> categoryEntityList = all.stream().filter((categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        })).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return categoryEntityList;
    }


    public List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntities, Long parentCid) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
    }
}