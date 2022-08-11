package com.user9527.shopping.search.thread;

import com.sun.javafx.collections.MappingChange;
import com.zaxxer.hikari.util.UtilityElf;
import javafx.application.Application;
import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.configuration.beanutils.BeanFactory;


import java.util.concurrent.*;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/14 7:25
 */
public class ThreadTest {
    // 当前系统中池只有一两个，每个异步任务，提交给线程池让它自己去执行就行
    public static ExecutorService service = Executors.newFixedThreadPool(10);

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 200, 10,
            TimeUnit.SECONDS ,
            new LinkedBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        ApplicationContext
        BeanFactory
        /**
         * 异步编排
         */
        System.out.println("main....start....");
        CompletableFuture.supplyAsync(() ->{
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor);
        System.out.println("main....end....");
    }

    public void thread() throws ExecutionException, InterruptedException {
        System.out.println("main....start....");
        /**
         * 1 继承 Thread
         *      Thread01 thread01 = new Thread01();
         *      thread01.start();
         * 2 实现 Runnable 接口
         *      Runnable01 runnable01 = new Runnable01();
         *      new Thread(runnable01).start();
         * 3 实现 Callable 接口 + FuTureTask
         *      FutureTask futureTask = new FutureTask<>(new Callable01());
         *      new Thread(futureTask).start();
         *      // 阻塞等待整个线程执行完成，获取返回结果
         *      Integer integer = (Integer) futureTask.get();
         * 4 线程池
         *
         *      区别
         *          1，2不能得到返回值。3可以得到返回值
         *          1，2，3都不能控制资源。
         *          4 可以控制资源
         */
//        Thread01 thread01 = new Thread01();
//        thread01.start();
//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();
//        FutureTask futureTask = new FutureTask<>(new Callable01());
//        new Thread(futureTask).start();
//        // 阻塞等待整个线程执行完成，获取返回结果
//        Integer integer = (Integer) futureTask.get();
//        service.execute(new Runnable01());
        /**
         * 线程池七大参数
         *  corePoolSize 核心线程数
         *  maximumPoolSize 最大线程数； 控制资源
         *  keepAliveTime 存活时间 空闲线程存活时间  maximumPoolSize - corePoolSize
         *  unit 时间单位
         *   BlockingQueue<Runnable> workQueu 阻塞队列 如果任务有很多，就会将目前多的任务放在队列里面
         *      只要有空闲线程，就会去队列里面取出新的任务继续执行
         *  threadFactory 线程的创建工厂
         *  RejectedExecutionHandler handler 如果队列满了，按照我们指定的拒绝策略拒绝执行任务
         *
         *
         *  工作顺序
         *      1    线程池创建好，准备好corePoolSize的线程数，准备接受任务
         *      1.1  corePoolSize满了，就将再进来的任务放进阻塞队列中，空闲的corePoolSize就会自己去阻塞队列里面获取任务执行
         *      1.2  阻塞队列满了，就会开启新线程，最大只能开到maximumPoolSize指定的数量
         *      1.3  maximumPoolSize满了就用RejectedExecutionHandler拒绝任务
         *      1.4  maximumPoolSize都执行完成，有很多空闲，在指定的时间keepAliveTime以后，释放maximumPoolSize - corePoolSize
         *
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 200, 10,
                TimeUnit.SECONDS ,
                new LinkedBlockingQueue<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        System.out.println("main....end....");
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            return i;



        }
    }
}
