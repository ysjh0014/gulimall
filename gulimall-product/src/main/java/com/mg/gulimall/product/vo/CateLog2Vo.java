package com.mg.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CateLog2Vo {
    private String CateLog1Id;//一级父分类id
    private List<CateLog3Vo> catalog3List;//三级子分类
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class CateLog3Vo {
        private String id;
        private String catalog2Id;//二级分类的id
        private String name;
    }
}
