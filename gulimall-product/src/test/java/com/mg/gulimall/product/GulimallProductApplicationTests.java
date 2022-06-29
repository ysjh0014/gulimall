package com.mg.gulimall.product;

import com.mg.gulimall.product.entity.BrandEntity;
import com.mg.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("测试");
        brandEntity.setName("苹果");
        brandEntity.setLogo("苹果");
        brandService.save(brandEntity);
    }

}
