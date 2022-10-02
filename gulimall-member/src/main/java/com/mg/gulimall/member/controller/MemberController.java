package com.mg.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.mg.common.exception.BizCodeEnume;
import com.mg.gulimall.member.exception.PhoneNumExistException;
import com.mg.gulimall.member.exception.UserExistException;
import com.mg.gulimall.member.feign.CouponFeignService;
import com.mg.gulimall.member.vo.MemberLoginVo;
import com.mg.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mg.gulimall.member.entity.MemberEntity;
import com.mg.gulimall.member.service.MemberService;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.R;


/**
 * 会员
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-21 17:15:58
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
            MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
            memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
            memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
            memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 获取当前会员的所有信息
     */
    @RequestMapping("/coupon")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("member",memberEntity).put("coupons",membercoupons.get("coupon"));
    }


    /**
     * 注册会员
     */
    /**
     * 注册会员
     * @return
     */
    @RequestMapping("/register")
    public R register(@RequestBody MemberRegisterVo registerVo) {
        try {
            memberService.register(registerVo);
        } catch (UserExistException userException) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMessage());
        } catch (PhoneNumExistException phoneException) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMessage());
        }
        return R.ok();
    }

    @RequestMapping("/login")
    public R login(@RequestBody MemberLoginVo loginVo) {
        MemberEntity entity=memberService.login(loginVo);
        if (entity!=null){
            return R.ok().put("data",entity);
        }else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
        }
    }
}
