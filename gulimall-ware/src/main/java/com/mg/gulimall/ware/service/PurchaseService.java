package com.mg.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.ware.entity.PurchaseEntity;
import com.mg.gulimall.ware.vo.PurchaseMergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-22 20:45:08
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils unreceive(Map<String, Object> params);

    void merge(PurchaseMergeVo purchaseMergeVo);

    void received(List<Long> items);
}

