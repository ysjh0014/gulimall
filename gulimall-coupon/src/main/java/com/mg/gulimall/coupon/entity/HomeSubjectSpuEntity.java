package com.mg.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 专题商品
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-17 21:46:28
 */
@Data
@TableName("sms_home_subject_spu")
public class HomeSubjectSpuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * id
         */
                @TableId
            private Long id;
            /**
         * 专题名字
         */
            private String name;
            /**
         * 专题id
         */
            private Long subjectId;
            /**
         * spu_id
         */
            private Long spuId;
            /**
         * 排序
         */
            private Integer sort;
    
}
