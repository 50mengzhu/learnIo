/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.entity;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.dx.io.mode.reactor.model.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 接收连接专用的 Handler
 *
 * @author mica
 */
public class AcceptHandler implements Handler {

    private static Log log = LogFactory.getLog(AcceptHandler.class);

    @Override
    public int interestOps() {
        return SelectionKey.OP_ACCEPT;
    }

    @Override
    public void handle(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = channel.accept();
            Selector selector = key.selector();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException ioe) {
            log.error("", ioe);
        }
    }
}
