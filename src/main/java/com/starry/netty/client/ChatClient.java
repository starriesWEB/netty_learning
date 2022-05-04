package com.starry.netty.client;

import com.starry.netty.message.*;
import com.starry.netty.protocol.MessageCodecSharable;
import com.starry.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/16 19:39
 * @Description
 */
@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {


                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                    //ch.pipeline().addLast(new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS));
                    //ch.pipeline().addLast(new ChannelDuplexHandler() {
                    //    @Override
                    //    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                    //        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                    //        // 触发 写空闲事件
                    //        if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                    //            log.info("3s 没有写数据了，发送一个心跳包");
                    //            ctx.channel().writeAndFlush(new PingMessage());
                    //        }
                    //    }
                    //});

                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    //ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodecSharable);
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("{}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage responseMessage = (LoginResponseMessage) msg;
                                if (responseMessage.isSuccess()) {
                                    // 登录成功
                                    isSuccess.set(true);
                                }
                                // 减一
                                countDownLatch.countDown();
                            }

                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();

                                // 构建消息对象
                                LoginRequestMessage requestMessage = new LoginRequestMessage(username, password);

                                ctx.writeAndFlush(requestMessage);

                                System.out.println("等待后续操作...");
                                try {
                                    // 阻塞，直到收到服务器回应
                                    countDownLatch.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!isSuccess.get()) {
                                    // 登录失败
                                    ctx.channel().close();
                                    return;
                                }
                                while (true) {
                                    // 登录成功
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            // 加入自己
                                            set.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }
                                }


                            }, "system in").start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
