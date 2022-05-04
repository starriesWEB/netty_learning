package com.starry.nio.network;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/29 21:36
 * @Description
 */
@Slf4j
public class ThreadServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        int cpus = Runtime.getRuntime().availableProcessors();
        log.info(String.valueOf(cpus));
        Worker[] workers = new Worker[cpus];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(i);
        }
        AtomicInteger number = new AtomicInteger();
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    // 将与客户端建立的channel和read_selector进行关联
                    workers[number.getAndIncrement() % workers.length].register(channel);
                }

                iterator.remove();
            }
        }
    }


    static class Worker implements Runnable {

        private Selector selector;
        private volatile boolean start = false;
        private int index;

        public Worker(int index) {
            this.index = index;
        }

        private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        @SneakyThrows
        public void register(SocketChannel socketChannel) {
            if (!start) {
                // 只执行一次
                selector = Selector.open();
                new Thread(this, "worker-" + index).start();
                start = true;
            }
            // 为了保证在同一线程中执行，使用队列
            tasks.add(() -> {
                try {
                    // 添加channel和read_selector的绑定
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            // 唤醒selector.select();
            selector.wakeup();
        }

        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                selector.select();
                Runnable task = tasks.poll();
                if (null != task) {
                    task.run();
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        try {
                            ByteBuffer buffer = ByteBuffer.allocate(128);
                            int len = channel.read(buffer);
                            if (len == -1) {
                                key.cancel();
                                channel.close();
                            } else {
                                buffer.flip();
                                String str = Charset.defaultCharset().decode(buffer).toString();
                                log.info(str);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                            channel.close();
                        }
                    }
                    iterator.remove();
                }
            }

        }
    }

}
