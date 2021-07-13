package com.user9527.shopping.product.web;

import com.user9527.shopping.product.entity.CategoryEntity;
import com.user9527.shopping.product.service.CategoryService;
import com.user9527.shopping.product.vo.Catelog2VO;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/5 19:48
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();

        model.addAttribute("categorys", categoryEntityList);
        // 试图解析器进行拼串
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2VO>> getCatelogJson() {
        Map<String, List<Catelog2VO>> catalogJson = categoryService.getCatelogJson();
        return catalogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        RLock lock = redisson.getLock("anyLock");
        // 加锁 阻塞式等待 默认加的锁都是30s时间
        // 1 锁的自动续期，如果业务超长，运行期间自动给锁上30s，不用担心业务时间长，锁自动过期被删掉
        // 2
        //
        lock.lock();
//        lock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {
        } finally {
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    @ResponseBody
    @GetMapping("/read")
    public String read() {
        RReadWriteLock rwlock = redisson.getReadWriteLock("anyRWLock");
        String s = "";
        try {
            // 读锁
            rwlock.readLock().lock();
            s = redisTemplate.opsForValue().get("my-writeLock");
            System.out.println("读取锁");
            Thread.sleep(10000);
        } catch (InterruptedException e) {

        } finally {
            rwlock.readLock().unlock();
        }

        return s;
    }

    @ResponseBody
    @GetMapping("/write")
    public String write() {
        /**
         * 1 写  读 ，写的时候 读取阻塞 互斥
         * 2 写 写  ，只能一个线程操作 互斥
         * 3 读 读  ，共享锁
         * 4 读 写  ，只能一个线程操作 互斥
         */
        String s = "";
        RReadWriteLock rwlock = redisson.getReadWriteLock("anyRWLock");
        try {
            // 写锁
            rwlock.writeLock().lock();
            s = UUID.randomUUID().toString();
            System.out.println("写入锁");
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("my-writeLock", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwlock.writeLock().unlock();
        }

        return s;
    }

    /**
     * 信号量 可以用作限流
     *
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        // 获取一个信号，获取一个值  -1  是阻塞的
//        park.acquire();
        // 不会阻塞 直接返回
        boolean b = park.tryAcquire();
        if (b) {
            // 执行业务
        } else {
            return "error" + b;
        }
        return "ok" + b;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redisson.getSemaphore("park");
        // 释放一个信号，+1
        park.release();
        return "ok";
    }

    /**
     * 闭锁
     * 所有的线程都走完才算完成
     * @return
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch anyCountDownLatch = redisson.getCountDownLatch("anyCountDownLatch");
        anyCountDownLatch.trySetCount(5);
        // 等待闭锁都完成
        anyCountDownLatch.await();
        return "放假了...";
    }

    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch anyCountDownLatch = redisson.getCountDownLatch("anyCountDownLatch");
        anyCountDownLatch.countDown();
        return id + "班的人都走了 ";
    }

}

