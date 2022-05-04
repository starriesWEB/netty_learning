package com.starry.nio.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/28 17:44
 * @Description
 */
public class TestByteBufferExam {


    /**
     * 网络上有多条数据发送给服务器，数据之间用\n进行分隔
     * 但是由于某种原因这些数据在接收时，被进行重新组合
     * 比如原始数据有3条
     *  Hello，world\n
     *  I'm zhangsan\n
     *  How are you?\n
     * 变成了下面的两个 byteBuffer（粘包，半包）
     *  Hello，world\nI'm zhangsan\nHo
     *  w are you?\n
     * 现在要求编写程序，将错乱的数据恢复成原始的按\n分隔的数据
     *
     * @param args
     */
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("Hello，world\nI'm zhangsan\nHo".getBytes(StandardCharsets.UTF_8));
        split(buffer);
        buffer.put("w are you?\n".getBytes(StandardCharsets.UTF_8));
        split(buffer);

    }

    /**
     * 打印分割好的数据
     * @param buffer
     */
    private static void split(ByteBuffer buffer) {
        // 切换为读模式
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            // 判断字符是否相等
            if ('\n' == buffer.get(i)) {
                // 取此条数据的长度,当前为\n的位置+1 减去 数据起始位置
                int length = i + 1 - buffer.position();
                ByteBuffer newBuffer = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    // 读取数据添加到新buffer
                    newBuffer.put(buffer.get());
                }
                newBuffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(newBuffer));
            }
        }
        // 压缩，将上次未读取完的数据移动到buffer头
        buffer.compact();

    }


}
