package com.starry.netty.protocol;

import com.starry.netty.config.Config;
import com.starry.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/9 20:43
 * @Description
 */
/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf,Message> {

    /**
     * 魔数
     */
    private static final byte[] MAGIC_NUMBER = new byte[]{1, 2, 3, 6};

    /**
     * 版本号
     */
    private static final byte VERSION = 1;



    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        // 4 字节，自定义 魔数
        buf.writeBytes(MAGIC_NUMBER);
        // 1 字节，版本号
        buf.writeByte(VERSION);
        // 1 字节，序列化算法 0 JDK、1 JSON
        buf.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 1 字节，指令类型
        buf.writeByte(msg.getMessageType());
        // 4 字节，请求序号
        buf.writeInt(msg.getSequenceId());
        // 1 字节，对齐填充，使固定长度为2的倍数
        buf.writeByte(0xff);

        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);

        // 4 字节，正文长度
        buf.writeInt(bytes.length);
        // 消息正文
        buf.writeBytes(bytes);

        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // 魔数判断
        byte[] magicNumber = new byte[MAGIC_NUMBER.length];
        msg.readBytes(magicNumber, 0, MAGIC_NUMBER.length);
        if (!Arrays.equals(MAGIC_NUMBER, magicNumber)) {
            throw new RuntimeException();
        }

        byte version = msg.readByte();
        // 序列化方式
        byte serializerType = msg.readByte();
        // 消息类型
        byte messageType = msg.readByte();
        int sequenceId = msg.readInt();
        msg.readByte();
        int length = msg.readInt();
        byte[] bytes = new byte[length];
        msg.readBytes(bytes, 0, length);

        // 具体序列化算法
        SerializerAlgorithm serializerAlgorithm = SerializerAlgorithm.values()[serializerType];
        // 获取消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = serializerAlgorithm.deSerialize(messageClass, bytes);

        log.info("{}|{}|{}|{}|{}|{}", magicNumber, version, serializerType, messageType, sequenceId, length);
        log.info("{}", message);

        // 放入 list 供下个 handler 处理
        out.add(message);
    }
}
