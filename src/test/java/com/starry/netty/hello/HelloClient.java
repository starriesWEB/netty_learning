package com.starry.netty.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/4 15:01
 * @Description
 */
public class HelloClient {


    public static void main(String[] args) throws InterruptedException {
        // 客户端启动器
        new Bootstrap()
                // selector
                .group(new NioEventLoopGroup())
                // channel 实现
                .channel(NioSocketChannel.class)
                // channel 的事件处理
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        // 添加 handle 将 String 转为 ByteBuf
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 连接地址
                .connect("localhost", 8080)
                // 同步阻塞，直到成功连接到服务器
                .sync()
                // 获取与服务器建立的 channel
                .channel()
                // 向 channel 写数据
                .writeAndFlush("hello world");
    }
}
