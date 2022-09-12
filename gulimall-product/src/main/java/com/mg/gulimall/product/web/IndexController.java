package com.mg.gulimall.product.web;

import com.mg.gulimall.product.entity.CategoryEntity;
import com.mg.gulimall.product.service.CategoryService;
import com.mg.gulimall.product.vo.CateLog2Vo;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 查出所有的一级分类
     * @param model
     * @return
     */
    @GetMapping({"/","/index.html"})
    public String index(Model model){
        List<CategoryEntity> categoryEntityList = categoryService.getLevelOne();
        //视图解析器进行拼串
        model.addAttribute("catagories",categoryEntityList);
        return "index";
    }


    /**
     * index/catalog.json
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<CateLog2Vo>> getCatalogJson(){
        Map<String, List<CateLog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }


    /**
     * redisson闭锁测试
     * @return
     */
    @GetMapping("/setLatch")
    @ResponseBody
    public String setLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("catelogJson");
        try {
            latch.trySetCount(5);
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "门栓被放开";
    }

    @GetMapping("/offLatch")
    @ResponseBody
    public String offLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("catelogJson");
        latch.countDown();
        return "门栓被放开1";
    }

}
