package com.mg.gulimall.auth.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mg.common.constant.AuthServerConstant;
import com.mg.common.utils.R;
import com.mg.common.vo.MemberResponseVo;
import com.mg.gulimall.auth.server.feign.MemberFeignService;
import com.mg.gulimall.auth.server.vo.UserLoginVo;
import com.mg.gulimall.auth.server.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo registerVo, BindingResult result, RedirectAttributes attributes) {
        //1.判断校验是否通过
        Map<String, String> errors = new HashMap<>();
        if (result.hasErrors()) {
            //1.1 如果校验不通过，则封装校验结果
            result.getFieldErrors().forEach(item -> {
                errors.put(item.getField(), item.getDefaultMessage());
                //1.2 将错误信息封装到session中
                attributes.addFlashAttribute("errors", errors);
            });
            //1.2 重定向到注册页
            return "redirect:http://localhost:10003/reg.html";
        } else {
            //2.若JSR303校验通过
            //判断验证码是否正确
//            String code = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());
            //2.1 如果对应手机的验证码不为空且与提交上的相等-》验证码正确
            /*if (!StringUtils.isEmpty(code) && registerVo.getCode().equals(code.split("_")[0])) {
                //2.1.1 使得验证后的验证码失效
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());

                //2.1.2 远程调用会员服务注册
                R r = memberFeignService.register(registerVo);
                if (r.getCode() == 0) {
                    //调用成功，重定向登录页
                    return "redirect:http://localhost:10003/login.html";
                }else {
                    //调用失败，返回注册页并显示错误信息
                    String msg = (String) r.get("msg");
                    errors.put("msg", msg);
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://localhost:10003/reg.html";
                }
            }else {
                //2.2 验证码错误
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://localhost:10003/reg.html";
            }*/
            //2.1.1 使得验证后的验证码失效
            //2.1.2 远程调用会员服务注册
            R r = memberFeignService.register(registerVo);
            if (r.getCode() == 0) {
                //调用成功，重定向登录页
                return "redirect:http://localhost:10003/login.html";
            } else {
                //调用失败，返回注册页并显示错误信息
                String msg = (String) r.get("msg");
                errors.put("msg", msg);
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://localhost:10003/reg.html";
            }
        }
    }

    /**
     * 登陆接口
     *
     * @param vo
     * @param attributes
     * @return
     */
    @RequestMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session){
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            String jsonString = JSON.toJSONString(r.get("memberEntity"));
//            MemberResponseVo memberResponseVo = JSON.parseObject(jsonString, new TypeReference<MemberResponseVo>() {
//            });
            MemberResponseVo data = r.getData("data",new TypeReference<MemberResponseVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://localhost:10001/";
        }else {
            String msg = (String) r.get("msg");
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", msg);
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://localhost:10003/login.html";
        }
    }

    @RequestMapping("/login.html")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute(AuthServerConstant.LOGIN_USER) != null) {
            MemberResponseVo attribute = (MemberResponseVo)session.getAttribute(AuthServerConstant.LOGIN_USER);
            model.addAttribute("session", attribute);
            return "redirect:http://localhost:10001/index.html";
        } else {
            return "login";
        }
    }

    @RequestMapping("/reg.html")
    public String reg() {
        return "reg";
    }
}
