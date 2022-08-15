package com.mg.gulimall.product.exception;

import com.mg.common.exception.BizCodeEnume;
import com.mg.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.mg.gulimall.product.controller")
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handler(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map = new HashMap();
        bindingResult.getFieldErrors().forEach((item)->{
            //获取报错信息
            String defaultMessage = item.getDefaultMessage();
            //获取报错属性字段名称
            String field = item.getField();
            map.put(field,defaultMessage);
        });
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMessage()).put("data",map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R exceptionHandler(Throwable throwable){
        log.error("错误",throwable);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(), BizCodeEnume.UNKNOW_EXCEPTION.getMessage());
    }
}
