package com.starry.netty.protocol;

import com.starry.netty.message.LoginRequestMessage;
import com.starry.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/9 21:18
 * @Description
 */
public class TestMessageCodec {


    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(LogLevel.DEBUG)
                ,new MessageCodecSharable()
        );

        Message message = new LoginRequestMessage("starry", "123acb");

        // encode
        channel.writeOutbound(message);

        // decode

        // 先将 Message 转为 ByteBuf，以便入站测试转换
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buffer);

        channel.writeInbound(buffer);

    }
}
