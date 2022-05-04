package com.starry.netty.server;

import com.starry.netty.protocol.MessageCodecSharable;
import com.starry.netty.protocol.ProtocolFrameDecoder;
import com.starry.netty.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler groupCreateRequestMessageHandler = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        QuitHandler quitHandler = new QuitHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    // IdleStateHandler 用来判断是不是 读空闲时间过长 或者 写空闲时间过长
                    // 5s 没有收到 channel 的数据，会触发一个事件 IdleState#READER_IDLE
                    //ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                    // ChannelDuplexHandler 可以同时作为入站和出站的处理器
                    //ch.pipeline().addLast(new ChannelDuplexHandler() {
                    //    /**
                    //     * 用来触发特殊事件
                    //     *
                    //     * @param ctx
                    //     * @param evt
                    //     * @throws Exception
                    //     */
                    //    @Override
                    //    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                    //        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                    //        // 读空闲事件
                    //        if (idleStateEvent.state() == IdleState.READER_IDLE) {
                    //            log.info("已经 5s 没有读到数据了");
                    //            ctx.channel().close();
                    //        }
                    //    }
                    //});

                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodecSharable);
                    ch.pipeline().addLast(loginRequestMessageHandler);
                    ch.pipeline().addLast(chatRequestMessageHandler);
                    ch.pipeline().addLast(groupCreateRequestMessageHandler);
                    ch.pipeline().addLast(groupChatRequestMessageHandler);
                    ch.pipeline().addLast(quitHandler);
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
