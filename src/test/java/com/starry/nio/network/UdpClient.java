package com.starry.nio.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/29 23:45
 * @Description
 */
public class UdpClient {


    public static void main(String[] args) {
        try (DatagramChannel channel = DatagramChannel.open()){
            ByteBuffer buffer = Charset.defaultCharset().encode("hello");
            InetSocketAddress address = new InetSocketAddress("localhost", 9999);
            channel.send(buffer, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
