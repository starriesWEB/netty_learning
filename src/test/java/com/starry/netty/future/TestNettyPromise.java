package com.starry.netty.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/5 13:57
 * @Description
 */
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        // 准备 EventLoop 对象
        EventLoop executors = group.next();
        // 创建 promise 结果容器
        Promise<Integer> promise = executors.newPromise();

        new Thread(() -> {
            // 任意线程执行计算，计算完向 promise 填充结果
            log.info("calculate...");
            try {
                //int i = 1 / 0;
                Thread.sleep(1000);
                promise.setSuccess(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();

        // 获取结果
        log.info("main thread waiting...");
        log.info("result:{}",promise.get());

        group.shutdownGracefully();


    }
}
