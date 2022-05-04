package com.starry.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/9 9:30
 * @Description
 */
public class TestRedisClient {


    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 换行 \r\n
        byte[] LINE = new byte[]{13, 10};

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        /**
                         * 建立连接后调用此方法
                         * @param ctx
                         * @throws Exception
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            set(ctx);
                            get(ctx);
                        }


                        /**
                         * 模拟 redis set 命令
                         * @param ctx
                         */
                        private void set(ChannelHandlerContext ctx) {
                            ByteBuf buffer = ctx.alloc().buffer();

                            buffer.writeBytes("*3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("set".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$4".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("name".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$6".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("starry".getBytes());
                            buffer.writeBytes(LINE);

                            ctx.writeAndFlush(buffer);
                        }

                        /**
                         * 模拟 redis get 命令
                         * @param ctx
                         */
                        private void get(ChannelHandlerContext ctx) {
                            ByteBuf buffer = ctx.alloc().buffer();

                            buffer.writeBytes("*2".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("get".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$4".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("name".getBytes());
                            buffer.writeBytes(LINE);

                            ctx.writeAndFlush(buffer);
                        }

                        /**
                         * 打印 redis 服务器返回的数据
                         * @param ctx
                         * @param msg
                         * @throws Exception
                         */
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println(buf.toString(Charset.defaultCharset()));
                        }
                    });
                }
            });


            ChannelFuture future = bootstrap.connect("localhost", 6379).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            group.shutdownGracefully();
        }
    }
}
