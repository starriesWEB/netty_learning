package com.starry.optimization;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/18 19:42
 * @Description
 */
@Slf4j
public class TestByteBuf {


    public static void main(String[] args) {
        new ServerBootstrap()
                .channel(ServerSocketChannel.class)
                .group(new NioEventLoopGroup())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ByteBuf buf = ch.alloc().buffer();
                        log.info("{alloc buf}", buf.getClass());
                    }
                });
    }
}
