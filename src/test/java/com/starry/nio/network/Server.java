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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/28 22:28
 * @Description
 */
@Slf4j
public class Server {


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


    public static void main(String[] args) throws Exception {
        // 创建selector，管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        // 建立selector和channel的联系（注册）,
        // SelectionKey就是将来事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 发生事件才继续运行，否则阻塞
            selector.select();
            // 处理事件,selectedKeys包含了所有发生的事件
            log.info(String.valueOf(selector.selectedKeys().size()));

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 判断事件类型
                if (key.isAcceptable()) {

                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);

                    ByteBuffer buffer = ByteBuffer.allocate(8);
                    // 将channel添加到selector，将buffer作为附属绑定到对应的key
                    SelectionKey channelKey = socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 获取附属的buffer
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int len = channel.read(buffer);
                        if (len == -1) {
                            // 客户端正常关闭
                            key.cancel();
                            channel.close();
                        } else {
                            // 输出内容
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                // 扩容
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity()*2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        // 客户端断开连接
                        e.printStackTrace();
                        key.cancel();
                    }
                }
                // 处理完key后，要删除
                iterator.remove();
                log.info("removed,{}",String.valueOf(selector.selectedKeys().size()));
            }
        }

    }




    private static void noBlocking() throws IOException {
        // 接收数据
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 是否阻塞，默认true
        ssc.configureBlocking(false);
        // 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        // channel集合
        List<SocketChannel> channelList = new ArrayList<>();

        while (true) {
            // 建立与客户端的连接，socketChannel用来和客户端间接通信
            // 配置了非阻塞，线程继续运行，如果没有连接，返回null
            SocketChannel socketChannel = ssc.accept();
            if (null != socketChannel) {
                log.info("connected:{}", socketChannel);
                channelList.add(socketChannel);
            }
            for (SocketChannel channel : channelList) {
                // 接收客户端发送的数据
                // 配置了非阻塞，线程继续运行，如果没有数据，返回0
                if (channel.read(buffer) > 0) {
                    buffer.flip();
                    log.info("data:{}", Charset.defaultCharset().decode(buffer));
                    buffer.clear();
                }
            }
        }
    }

    private static void blocking() throws IOException {
        // 接收数据
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        // channel集合
        List<SocketChannel> channelList = new ArrayList<>();

        while (true) {
            // 建立与客户端的连接，socketChannel用来和客户端间接通信
            // 阻塞方法，线程停止运行
            SocketChannel socketChannel = ssc.accept();
            log.info("connected:{}", socketChannel);
            channelList.add(socketChannel);
            for (SocketChannel channel : channelList) {
                // 接收客户端发送的数据
                // 阻塞方法，线程停止运行
                channel.read(buffer);
                buffer.flip();
                log.info("data:{}", Charset.defaultCharset().decode(buffer));
                buffer.clear();
            }
        }
    }
}
