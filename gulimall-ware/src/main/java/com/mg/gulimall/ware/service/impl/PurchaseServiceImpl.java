package com.mg.gulimall.ware.service.impl;

import com.mg.common.constant.PurchaseConstant;
import com.mg.gulimall.ware.entity.PurchaseDetailEntity;
import com.mg.gulimall.ware.service.PurchaseDetailService;
import com.mg.gulimall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.ware.dao.PurchaseDao;
import com.mg.gulimall.ware.entity.PurchaseEntity;
import com.mg.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils unreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }


    @Transactional
    @Override
    public void merge(PurchaseMergeVo purchaseMergeVo) {
        Long purchaseId = purchaseMergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(PurchaseConstant.PurchaseStatus.CREATE.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        //TODO 确认要合并的采购单状态是0或者1
        List<Long> items = purchaseMergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseEntity byId = this.getById(item);
            return byId;
        }).filter(m->{
            return m.getStatus().equals(PurchaseConstant.PurchaseDetailStatus.CREATE.getCode())||m.getStatus().equals(PurchaseConstant.PurchaseDetailStatus.ASSINGED.getCode());
        }).map(p->{
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(p.getId());
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(PurchaseConstant.PurchaseDetailStatus.ASSINGED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> items) {
        //1.确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = items.stream().map(item -> {
            PurchaseEntity purchaseEntity = this.getById(item);
            return purchaseEntity;
        }).filter(pur -> {
            return pur.getStatus() == PurchaseConstant.PurchaseStatus.CREATE.getCode() || pur.getStatus() == PurchaseConstant.PurchaseStatus.ASSINGED.getCode();
        }).map(purch -> {
            purch.setStatus(PurchaseConstant.PurchaseStatus.RECEIVE.getCode());
            purch.setUpdateTime(new Date());
            return purch;
        }).collect(Collectors.toList());
        //2.改变采购单的状态
        this.updateBatchById(collect);
        //3.改变采购项的状态
        collect.forEach(detail->{
            List<PurchaseDetailEntity> details = purchaseDetailService.getDetailByPurchaseId(detail.getId());
            List<PurchaseDetailEntity> purchaseDetailEntities = details.stream().map(item -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(item.getId());
                detailEntity.setStatus(PurchaseConstant.PurchaseDetailStatus.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });

    }

}