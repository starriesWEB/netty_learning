package com.starry.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/4 21:33
 * @Description
 */
@Slf4j
public class TestChannelFuture {


    public static void main(String[] args) throws InterruptedException, IOException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 异步非阻塞，main 线程发起调用，NioEventLoopGroup 真正执行
                .connect("localhost", 8080);

        // 连接异步回调
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.info("{}", channel);
                channel.writeAndFlush("hello");
            }
        });


        //Channel channel = channelFuture.channel();
        //log.info("{}",channel);
        //// 阻塞，直到成功连接服务器
        //channelFuture.sync();
        //channel = channelFuture.channel();
        //log.info("{}",channel);

    }

}
