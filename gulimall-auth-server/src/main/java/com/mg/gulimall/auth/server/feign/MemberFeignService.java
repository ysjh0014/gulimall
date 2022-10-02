package com.mg.gulimall.auth.server.feign;

import com.mg.common.utils.R;
import com.mg.gulimall.auth.server.vo.UserLoginVo;
import com.mg.gulimall.auth.server.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gulimall-member")
public interface MemberFeignService {
    @RequestMapping("member/member/register")
    R register(@RequestBody UserRegisterVo registerVo);

    @RequestMapping("member/member/login")
    R login(@RequestBody UserLoginVo loginVo);
}
