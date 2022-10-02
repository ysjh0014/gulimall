package com.mg.gulimall.product.web;

import com.mg.gulimall.product.entity.CategoryEntity;
import com.mg.gulimall.product.service.CategoryService;
import com.mg.gulimall.product.vo.CateLog2Vo;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 查出所有的一级分类
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String index(Model model, HttpSession session) {
        List<CategoryEntity> categoryEntityList = categoryService.getLevelOne();
        //视图解析器进行拼串
        model.addAttribute("catagories", categoryEntityList);
        return "index";
    }


    /**
     * index/catalog.json
     *
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<CateLog2Vo>> getCatalogJson() {
        Map<String, List<CateLog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    /**
     * redisson可重入锁测试
     * 1）、锁的自动续期，如果业务时间超长，运行期间自动给锁续上新的30s，不用担心业务时间长，锁自动过期被删掉
     * 2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除
     * <p>
     * 问题：Lock.lock(10,TimeUnit.SECONDS);在锁时间到了之后，不会自动续期
     * 1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
     * 2、如果我们未指定锁的超时时间，就是用30*1000【LockWatchdogTimeout看门狗的默认时间】
     * <p>
     * 只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】，每隔10s都会自动再次续期，续成30s
     * internalLockLeaseTime【看门狗时间】/
     */
    @ResponseBody
    @GetMapping("hello")
    public String hello() {
        //只要锁的名字相同，就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");
        //加锁
//        lock.lock(); //阻塞式等待

        lock.lock(10, TimeUnit.SECONDS);//10s自动解锁，自动解锁时间一定要大于业务执行的时间

        try {
            System.out.println("获取到锁。。。。" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            System.out.println("解锁。。。。" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }


    /**
     * 读锁
     */
    @GetMapping("read")
    @ResponseBody
    public String read() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.readLock();
        String s = "";
        rLock.lock();
        try {
            s = redisTemplate.opsForValue().get("value");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 写锁
     *
     *  保证一定能读到最新数据，修改期间，写锁是一个排他锁(互斥锁，独享锁)，读锁是一个共享锁
     *  写锁没释放读就必须等待
     *
     *  读+读：相当于无锁，并发读，只会在redis中记录好，所有当前的读锁。他们都会加锁成功
     *  写+读：等待写锁释放
     *  写+写：阻塞方式
     *  读+写：有读锁，写也需要等待
     *  只要有写的存在，都必须等待
     * @return
     */
    @GetMapping("write")
    @ResponseBody
    public String write() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        String s = UUID.randomUUID().toString();
        rLock.lock();
        try {
            redisTemplate.opsForValue().set("value",s);
            Thread.sleep(3000);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }




    /**
     * redisson闭锁测试
     *
     * @return
     */
    @GetMapping("/setLatch")
    @ResponseBody
    public String setLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("catelogJson");
        try {
            latch.trySetCount(5);
            latch.await();    //等待闭锁都完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "门栓被放开";
    }

    @GetMapping("/offLatch")
    @ResponseBody
    public String offLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("catelogJson");
        latch.countDown();     //计数减一
        return "门栓被放开1";
    }

}
