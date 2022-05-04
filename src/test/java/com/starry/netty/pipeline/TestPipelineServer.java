package com.starry.netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/5 14:22
 * @Description
 */
public class TestPipelineServer {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                System.out.println(1);
                                ctx.fireChannelRead(msg); // 1
                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                System.out.println(2);
                                ctx.fireChannelRead(msg); // 2
                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                System.out.println(3);
                                ctx.channel().write(msg); // 3
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg,
                                              ChannelPromise promise) {
                                System.out.println(4);
                                ctx.write(msg, promise); // 4
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg,
                                              ChannelPromise promise) {
                                System.out.println(5);
                                ctx.write(msg, promise); // 5
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg,
                                              ChannelPromise promise) {
                                System.out.println(6);
                                ctx.write(msg, promise); // 6
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
