package com.mg.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

    import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-22 20:45:08
 */
@Data
@TableName("undo_log")
public class UndoLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

            /**
         * 
         */
                @TableId
            private Long id;
            /**
         * 
         */
            private Long branchId;
            /**
         * 
         */
            private String xid;
            /**
         * 
         */
            private String context;
            /**
         * 
         */
            private Longblob rollbackInfo;
            /**
         * 
         */
            private Integer logStatus;
            /**
         * 
         */
            private Date logCreated;
            /**
         * 
         */
            private Date logModified;
            /**
         * 
         */
            private String ext;
    
}
