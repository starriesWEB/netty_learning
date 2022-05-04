package com.starry.netty.server.protocol;

import com.starry.netty.config.Config;
import com.starry.netty.message.LoginRequestMessage;
import com.starry.netty.message.Message;
import com.starry.netty.protocol.MessageCodecSharable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/17 19:59
 * @Description
 */
public class TestProtocol {

    public static void main(String[] args) {
        MessageCodecSharable codec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(loggingHandler, codec, loggingHandler);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        //embeddedChannel.writeOutbound(message);

        ByteBuf buf = messageToBytes(message);
        embeddedChannel.writeInbound(buf);
    }

    static ByteBuf messageToBytes(Message msg) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{1,2,3,6});
        buf.writeByte(1);
        buf.writeByte(Config.getSerializerAlgorithm().ordinal());
        buf.writeByte(msg.getMessageType());
        buf.writeInt(msg.getSequenceId());
        buf.writeByte(0xff);
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);

        return buf;
    }
}
