package com.mg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.product.entity.SpuInfoEntity;
import com.mg.gulimall.product.vo.SpuInfoVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBathInfo(SpuInfoVo spuInfoVo);

    PageUtils queryPageByParams(Map<String, Object> params);

    void up(Long spuId);
}

