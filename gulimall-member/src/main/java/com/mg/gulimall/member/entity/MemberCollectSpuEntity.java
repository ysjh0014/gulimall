package com.mg.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 会员收藏的商品
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-21 17:15:58
 */
@Data
@TableName("ums_member_collect_spu")
public class MemberCollectSpuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * id
         */
                @TableId
            private Long id;
            /**
         * 会员id
         */
            private Long memberId;
            /**
         * spu_id
         */
            private Long spuId;
            /**
         * spu_name
         */
            private String spuName;
            /**
         * spu_img
         */
            private String spuImg;
            /**
         * create_time
         */
            private Date createTime;
    
}
