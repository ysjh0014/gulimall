package com.mg.gulimall.member.dao;

import com.mg.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-21 17:15:58
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

}
