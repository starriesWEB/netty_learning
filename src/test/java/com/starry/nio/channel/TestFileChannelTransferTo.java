package com.starry.nio.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/28 18:40
 * @Description
 */
public class TestFileChannelTransferTo {

    public static void main(String[] args) {

        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {

            // 效率高，底层利用操作系统的零拷贝进行优化，只能传输2g的数据
            long size = from.size();
            // size 需要传输的总数据
            // last 当前剩余的数据（还未进行传输的数据）
            for (long last = size; last > 0; ) {
                // 开始位置就是总的减剩余的
                // 结果：剩余的减去本次传输的
                last -= from.transferTo(size - last, size, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
