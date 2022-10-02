package com.mg.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.member.entity.MemberEntity;
import com.mg.gulimall.member.vo.MemberLoginVo;
import com.mg.gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-21 17:15:58
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo registerVo);

    MemberEntity login(MemberLoginVo loginVo);
}

