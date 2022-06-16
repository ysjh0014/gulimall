package com.mg.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 品牌
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * 品牌id
         */
                @TableId
            private Long brandId;
            /**
         * 品牌名
         */
            private String name;
            /**
         * 品牌logo地址
         */
            private String logo;
            /**
         * 介绍
         */
            private String descript;
            /**
         * 显示状态[0-不显示；1-显示]
         */
            private Integer showStatus;
            /**
         * 检索首字母
         */
            private String firstLetter;
            /**
         * 排序
         */
            private Integer sort;
    
}
