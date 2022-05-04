package com.starry.nio.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/29 20:19
 * @Description
 */
@Slf4j
public class WriteServer {


    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int len = channel.write(buffer);
                    log.info(String.valueOf(len));
                    if (buffer.hasRemaining()) {
                        // read 1  write 4
                        // 在原有关注事件的基础上，多关注 写事件
                        key.interestOps(key.interestOps() + SelectionKey.OP_WRITE);
                        key.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();

                    int len = channel.write(buffer);
                    log.info(String.valueOf(len));
                    if (!buffer.hasRemaining()) {
                        // 写完了，取消注册写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        // 清空附属，help buffer gc
                        key.attach(null);
                    }
                }
            }
            iterator.remove();
        }
    }
}
