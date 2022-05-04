package com.starry.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/4 14:36
 * @Description
 */
public class HelloServer {


    public static void main(String[] args) {
        // 启动器，负责组装 netty 各个组件，启动服务器
        new ServerBootstrap()
                // group 组，简单理解未 线程池+selector
                .group(new NioEventLoopGroup())
                // 选择服务器的 ServerSocketChannel 实现，linux、mac 等等，这里选择通用的 nio
                .channel(NioServerSocketChannel.class)
                // 添加子处理器给 SocketChannel 处理事件，能添加多个
                .childHandler(
                        // channel 代表和客户端进行数据读写的通道
                        // Initializer 初始化，负责添加其他的handle，在连接建立后会被调用
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 添加具体的 handle
                                // 将 ByteBuf 转为字符串
                                nioSocketChannel.pipeline().addLast(new StringDecoder());
                                // 自定义的 handle
                                nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                    // 读事件
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
                                        System.out.println(msg);

                                    }
                                });
                            }
                        })
                // 绑定监听端口
                .bind(8080);
    }
}
