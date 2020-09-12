/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import com.dx.io.mode.reactor.model.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 专门处理 write事件的 handler
 *
 * @author mica
 */
public class WriteHandler implements Handler {

    private static Log log = LogFactory.getLog(WriteHandler.class);

    @Override
    public int interestOps() {
        return SelectionKey.OP_WRITE;
    }

    @Override
    public void handle(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        final String message = "hello world!";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        log.info("prepare to write!");
        try {
            int write = channel.write(buffer);
        } catch (IOException ioe) {
            log.error("", ioe);
            try {
                channel.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }
}
