package com.starry.nio.network;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


public class Client {


    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
        //socketChannel.write(Charset.defaultCharset().encode("你好hello world,abc123@\n"));
        socketChannel.write(Charset.defaultCharset().encode("1234567890\n"));
        //socketChannel.close();
        System.out.println("waiting...");

    }
}
