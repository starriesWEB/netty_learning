package com.starry.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/6 22:05
 * @Description
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});
        System.out.println(ByteBufUtil.prettyHexDump(buf1));
        System.out.println(ByteBufUtil.prettyHexDump(buf2));

        //ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer(buf1.readableBytes() + buf2.readableBytes());
        //buf3.writeBytes(buf1);
        //buf3.writeBytes(buf2);
        //System.out.println(ByteBufUtil.prettyHexDump(buf3));

        CompositeByteBuf buf3 = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf3.addComponents(true, buf1, buf2);


    }
}
