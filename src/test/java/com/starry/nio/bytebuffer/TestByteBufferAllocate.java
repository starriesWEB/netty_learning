package com.starry.nio.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/27 14:10
 * @Description
 */
public class TestByteBufferAllocate {


    public static void main(String[] args) {
        /*
        * class java.nio.HeapByteBuffer     java堆内存，读写效率低，受到GC的影响
        * class java.nio.DirectByteBuffer   直接内存（系统内存），读写效率高（少一次拷贝），不受GC的影响，但是分配时效率较低
        *
        * */
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());

        ByteBuffer buffer = ByteBuffer.allocate(16);


    }
}
