package com.starry.netty.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/5 13:48
 * @Description
 */
@Slf4j
public class TestNettyFuture {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        EventLoop executors = group.next();

        Future<Integer> future = executors.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("calculate...");
                Thread.sleep(1000);
                return 50;
            }
        });

        log.info("main thread waiting...");
        // 同步
        //log.info("result:{}",future.get());

        // 异步
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.info("result:{}",future.getNow());
                group.shutdownGracefully();
            }
        });


    }
}
