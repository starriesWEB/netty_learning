package com.starry.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/7 14:32
 * @Description
 */
public class TestLengthFieldBasedFrameDecoder {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                /*
                * maxFrameLength – 帧的最大长度。如果帧的长度大于这个值，就会抛出 TooLongFrameException。
                * lengthFieldOffset – 长度字段的偏移量
                * lengthFieldLength – 长度字段的长度
                * lengthAdjustment – 添加到长度字段值的补偿值
                * initialBytesToStrip – 从解码帧中剥离的第一个字节数
                * */
                //new LengthFieldBasedFrameDecoder(1024, 0, 4, 4, 8),
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 4, 0),
                new LoggingHandler(LogLevel.DEBUG)
        );
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        write(buffer,"hello, world",1);
        write(buffer,"hello,world",2);
        channel.writeInbound(buffer);
    }

    private static void write(ByteBuf buf, String content,int version) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        buf.writeInt(length);
        buf.writeInt(version);
        buf.writeBytes(bytes);
    }
}
