package com.starry.nio.network;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/29 23:47
 * @Description
 */
public class UdpServer {


    public static void main(String[] args) {
        try (DatagramChannel channel = DatagramChannel.open();) {

            channel.socket().bind(new InetSocketAddress(9999));
            ByteBuffer buffer = ByteBuffer.allocate(32);
            SocketAddress receive = channel.receive(buffer);
            buffer.flip();
            String str = Charset.defaultCharset().decode(buffer).toString();
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
