package com.starry.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/27 23:40
 * @Description
 */
public class TestScatteringReads {

    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            ByteBuffer buffer1 = ByteBuffer.allocate(3);
            ByteBuffer buffer2 = ByteBuffer.allocate(3);
            ByteBuffer buffer3 = ByteBuffer.allocate(5);

            channel.read(new ByteBuffer[]{buffer1, buffer2, buffer3});
            buffer1.flip();
            buffer2.flip();
            buffer3.flip();

            System.out.println(StandardCharsets.UTF_8.decode(buffer1));
            System.out.println(StandardCharsets.UTF_8.decode(buffer2));
            System.out.println(StandardCharsets.UTF_8.decode(buffer3));

        } catch (IOException e) {
        }
    }
}
