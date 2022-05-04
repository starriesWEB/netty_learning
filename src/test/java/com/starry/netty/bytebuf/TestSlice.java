package com.starry.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/6 21:58
 * @Description
 */
public class TestSlice {

    public static void main(String[] args) {
        ByteBuf origin = ByteBufAllocator.DEFAULT.buffer(10);
        origin.writeBytes(new byte[]{1, 2, 3, 4});
        origin.readByte();
        System.out.println(ByteBufUtil.prettyHexDump(origin));
        ByteBuf slice = origin.slice();
        System.out.println(ByteBufUtil.prettyHexDump(slice));
        //slice.writeInt(1);
        origin.readByte();
        System.out.println(ByteBufUtil.prettyHexDump(origin));
        System.out.println(ByteBufUtil.prettyHexDump(slice));

    }
}
