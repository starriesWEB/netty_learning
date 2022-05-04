package com.starry.netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/4 19:49
 * @Description
 */
@Slf4j
public class EventLoopServer {


    public static void main(String[] args) {
        // 新增一个 EventLoopGroup 用来处理事件
        DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
        new ServerBootstrap()
                // 2个 EventLoopGroup
                // 参数一的只处理 ServerSocketChannel 的 accept 事件
                // 参数二的只处理 SocketChannel 的 read、write事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("handle1",new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf byteBuf = (ByteBuf) msg;
                                        log.info(byteBuf.toString(StandardCharsets.UTF_8));
                                        // 将消息传递到下一个 handle
                                        ctx.fireChannelRead(msg);
                                    }
                                })
                                // 假设事件处理时间较长，可以使用额外 EventLoopGroup 来处理
                                .addLast(group,"handle2",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf byteBuf = (ByteBuf) msg;
                                        log.info(byteBuf.toString(StandardCharsets.UTF_8));
                                    }
                                })
                        ;
                    }
                })
                .bind(8080);
    }
}
