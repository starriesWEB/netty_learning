package com.starry.netty.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/5 13:40
 * @Description
 */
@Slf4j
public class TestJdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.SECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        // 提交任务
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("calculate...");
                Thread.sleep(1000);
                return 50;
            }
        });

        // 获取结果
        log.info("main thread waiting...");
        log.info("result:{}",future.get());

        executor.shutdown();
    }
}
