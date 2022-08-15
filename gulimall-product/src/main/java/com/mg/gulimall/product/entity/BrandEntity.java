package com.mg.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;

import com.mg.common.valid.AddGroup;
import com.mg.common.valid.ListValue;
import com.mg.common.valid.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

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
            @NotNull(message = "修改时必须指定品牌id",groups = {UpdateGroup.class})
            @Null(message = "新增是不能指定id",groups = {AddGroup.class})
            @TableId
            private Long brandId;
            /**
         * 品牌名
         */
            @NotBlank(message = "品牌名不能为空",groups = {AddGroup.class,UpdateGroup.class})
            private String name;
            /**
         * 品牌logo地址
         */
            @NotBlank(message = "logo地址不能为空",groups = {AddGroup.class})
            @URL(message = "logo地址必须是一个合法的URL地址",groups = {AddGroup.class, UpdateGroup.class})
            private String logo;
            /**
         * 介绍
         */
            private String descript;
            /**
         * 显示状态[0-不显示；1-显示]
         */
            @NotBlank(message = "显示开关不能为空")
            @ListValue(message = "只能是0或者1",vals={0,1},groups = {AddGroup.class, UpdateGroup.class})
            private Integer showStatus;
            /**
         * 检索首字母
         */
            @NotEmpty(groups={AddGroup.class})
            @Pattern(regexp="^[a-zA-Z]$",message = "检索首字母必须是一个字母",groups={AddGroup.class,UpdateGroup.class})
            private String firstLetter;
            /**
         * 排序
         */
            @NotNull(groups = {AddGroup.class})
            @Min(value = 0,message = "排序必须是一个大于等于0的数字",groups = {AddGroup.class,UpdateGroup.class})
            private Integer sort;
    
}
