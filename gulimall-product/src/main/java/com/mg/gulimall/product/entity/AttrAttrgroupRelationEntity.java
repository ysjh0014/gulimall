package com.mg.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 属性&属性分组关联
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
@Data
@TableName("pms_attr_attrgroup_relation")
public class AttrAttrgroupRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * id
         */
                @TableId
            private Long id;
            /**
         * 属性id
         */
            private Long attrId;
            /**
         * 属性分组id
         */
            private Long attrGroupId;
            /**
         * 属性组内排序
         */
            private Integer attrSort;
    
}
