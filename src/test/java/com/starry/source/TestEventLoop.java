package com.starry.source;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author starry
 * @version 1.0
 * @date 2022/3/26 18:26
 * @Description
 */
public class TestEventLoop {

    public static void main(String[] args) {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        eventLoop.execute(()->{
            System.out.println("running1");
        });
        eventLoop.execute(()->{
            System.out.println("running2");
        });
    }
}
