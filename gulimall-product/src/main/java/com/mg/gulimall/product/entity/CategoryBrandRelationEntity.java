package com.mg.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 品牌分类关联
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:52
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * 
         */
                @TableId
            private Long id;
            /**
         * 品牌id
         */
            private Long brandId;
            /**
         * 分类id
         */
            private Long catelogId;
            /**
         * 
         */
            private String brandName;
            /**
         * 
         */
            private String catelogName;
    
}
