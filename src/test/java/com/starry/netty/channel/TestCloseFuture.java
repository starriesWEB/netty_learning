package com.starry.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/4 21:33
 * @Description
 */
@Slf4j
public class TestCloseFuture {


    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 日志 handle
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8080);

        Channel channel = channelFuture.sync().channel();

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    // 如果输入 q 就关闭 channel
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();


        ChannelFuture closeFuture = channel.closeFuture();
        //// 同步阻塞，直到 channel 被关闭
        //closeFuture.sync();
        //log.info("do something...");
        //// 关闭 eventLoopGroup
        //eventLoopGroup.shutdownGracefully();


        // 关闭异步回调
        closeFuture.addListener((ChannelFutureListener) future -> {
            log.info("do something...");
            // 关闭 eventLoopGroup
            eventLoopGroup.shutdownGracefully();
        });


    }

}
