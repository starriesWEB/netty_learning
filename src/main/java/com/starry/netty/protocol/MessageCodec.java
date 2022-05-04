package com.starry.netty.protocol;

import com.starry.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/9 20:43
 * @Description
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 魔数
     */
    private static final byte[] MAGIC_NUMBER = new byte[]{1, 2, 3, 6};

    /**
     * 版本号
     */
    private static final byte VERSION = 1;


    /**
     * 出站前 将 Message 编码为 ByteBuf
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 4 字节，自定义 魔数
        out.writeBytes(MAGIC_NUMBER);
        // 1 字节，版本号
        out.writeByte(VERSION);
        // 1 字节，序列化算法 0 JDK、1 JSON
        out.writeByte(0);
        // 1 字节，指令类型
        out.writeByte(msg.getMessageType());
        // 4 字节，请求序号
        out.writeInt(msg.getSequenceId());
        // 1 字节，对齐填充，使固定长度为2的倍数
        out.writeByte(0xff);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(outputStream);
        stream.writeObject(msg);
        byte[] bytes = outputStream.toByteArray();

        // 4 字节，正文长度
        out.writeInt(bytes.length);
        // 消息正文
        out.writeBytes(bytes);

    }

    /**
     * 入站后 将 ByteBuf 编码为 Message
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        byte[] magicNumber = new byte[MAGIC_NUMBER.length];
        in.readBytes(magicNumber, 0, MAGIC_NUMBER.length);
        if (!Arrays.equals(MAGIC_NUMBER, magicNumber)) {
            throw new RuntimeException();
        }

        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) inputStream.readObject();

        log.info("{}|{}|{}|{}|{}|{}", magicNumber, version, serializerType, messageType, sequenceId, length);
        log.info("{}", message);

        // 放入 list 供下个 handler 处理
        out.add(message);
    }
}
