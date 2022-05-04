package com.starry.netty.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/4 19:31
 * @Description
 */
@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        // 可处理 IO 事件，普通任务，定时任务
        // 未指定就查看环境变量是否配置了io.netty.eventLoopThreads，否则就是 系统线程数*2，如果都没有就是1
        EventLoopGroup group = new NioEventLoopGroup(2);
        // 可处理 普通任务，定时任务
        //EventLoopGroup group = new DefaultEventLoop();

        // 获取下一个事件循环对象
        for (EventExecutor eventExecutor : group) {
            System.out.println(eventExecutor);
        }

        // 执行普通任务
        group.next().execute(() -> {
            try {
                Thread.sleep(1000);
                log.info("task");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.info("schedule");
        }, 0, 1, TimeUnit.SECONDS);

        log.info("main");

        // 关闭，并非立即关闭
        group.shutdownGracefully();
    }
}
