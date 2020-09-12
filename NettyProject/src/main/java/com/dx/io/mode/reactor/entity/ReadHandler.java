/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.dx.io.mode.reactor.model.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 专门用来处理 Read事件的 Handler
 *
 * @author mica
 */
public class ReadHandler implements Handler {

    private static Log log = LogFactory.getLog(ReadHandler.class);

    public static final Integer SIZE_BUFFER = 1024;

    @Override
    public int interestOps() {
        return SelectionKey.OP_READ;
    }

    @Override
    public void handle(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(SIZE_BUFFER);
        try {
            int len = channel.read(buffer);
            // 测试单线程阻塞的代码
            Thread.sleep(20 * 1000);
            if (len > 0) {
                buffer.flip();
                log.info(new String(buffer.array(), 0, len));
                buffer.clear();
            } else {
                // 对于 telnet作为客户端，在 channel#read的时候不会抛出异常
                // channel#read返回值为 -1
                throw new IOException("can not read!");
            }
        } catch (IOException ioe) {
            try {
                log.error(String.format("%s is online", channel.getRemoteAddress()), ioe);
                channel.close();
            } catch (IOException e) {
                log.error("", e);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
