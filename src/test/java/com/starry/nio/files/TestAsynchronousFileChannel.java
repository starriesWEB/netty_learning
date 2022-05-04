package com.starry.nio.files;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/30 21:51
 * @Description
 */
@Slf4j
public class TestAsynchronousFileChannel {


    public static void main(String[] args) throws IOException {
        Path file = Paths.get("data.txt");
        StandardOpenOption option = StandardOpenOption.READ;
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(file, option)) {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.info("begin");
            // 参数一：读取文件后要写入的buffer
            // 参数二：从哪个位置开始读
            // 参数三：附属对象
            // 参数四：回调方法
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                /**
                 * read success
                 * @param result
                 * @param attachment
                 */
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.info("read success ==> length:{}", result);
                    attachment.flip();
                    String str = Charset.defaultCharset().decode(attachment).toString();
                    log.info("result:{}", str);

                }

                /**
                 * read failed
                 * @param exc
                 * @param attachment
                 */
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    log.error(exc.getMessage(), exc);
                }
            });
        } catch (IOException e) {
        }
        log.info("end");
        System.in.read();
    }
}
