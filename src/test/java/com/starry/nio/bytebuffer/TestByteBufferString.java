package com.starry.nio.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/27 23:21
 * @Description
 */
public class TestByteBufferString {

    public static void main(String[] args) {
        String str = "hello";

        // 1、字符串转 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(str.getBytes(StandardCharsets.UTF_8));
        // 此时还是写模式，需要手动执行buffer.flip()

        // 2、Charset
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode(str);
        // 此方法会自动切换到读模式

        // 3、wrap
        ByteBuffer buffer3 = ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8));
        // 此方法会自动切换到读模式


        // ByteBuffer转字符串
        buffer.flip();
        String s1 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(s1);

        String s2 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(s2);

        String s3 = StandardCharsets.UTF_8.decode(buffer3).toString();
        System.out.println(s3);


    }
}
