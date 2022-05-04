package com.starry.nio.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/29 20:27
 * @Description
 */
public class WriteClient {


    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8080));
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        int count = 0;
        while (true) {
            count += socketChannel.read(buffer);
            System.out.println(count);
            buffer.clear();
        }


    }
}
