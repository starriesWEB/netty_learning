package com.starry.nio.bytebuffer;

import cn.hutool.core.lang.Console;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/27 11:56
 * @Description
 */
public class TestByteBuffer {


    public static void main(String[] args) {
        // FileChannel
        // 输入输出流
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);

            // 从channel读取数据，写入到buffer
            while (channel.read(buffer) != -1) {
                /*
                切换为读模式，
                limit = position; （读）截至位置为原当前位置
                position = 0;     原位置从0开始（读）
                */
                buffer.flip();
                // 还有剩余
                while (buffer.hasRemaining()) {
                    // 默认读取一个字节
                    byte b = buffer.get();
                    Console.log("读取到的字节:{}", (char) b);
                }
                // 清除缓冲区
                buffer.clear();
            }

        } catch (IOException e) {
        }

        System.out.println("1111");
        test();
    }


    static void test() {
        // FileChannel
        // 输入输出流
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);

            int read = channel.read(buffer);
            buffer.flip();
            byte a = buffer.get(8);
            buffer.compact();
            buffer.put(new byte[]{90});

            buffer.flip();
            while (buffer.hasRemaining()) {
                // 默认读取一个字节
                byte b = buffer.get();
                Console.log("读取到的字节:{}", (char) b);
            }

        } catch (IOException e) {
        }
    }
}
