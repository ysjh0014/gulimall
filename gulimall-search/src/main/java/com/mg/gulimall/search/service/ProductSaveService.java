package com.mg.gulimall.search.service;

import com.mg.common.to.SkuModelVo;
import com.mg.common.utils.PageUtils;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuModelVo> skuModelVoList) throws IOException;
}
