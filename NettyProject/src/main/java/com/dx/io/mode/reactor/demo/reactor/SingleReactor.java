/**
 * copyright@doug lea
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.demo.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.dx.io.mode.reactor.demo.handler.Handler;

/**
 * Reactor模式，是抽象出两个主要的处理类 ——  Reactor和 Handler，
 * Reactor接收客户端的请求并进行对请求进行分发
 * Handler则是真正对请求进行处理的对象
 * <p>
 * 本类实现的是，单线程版本的 Reactor模式，也就是说，Reactor轮询线程和处理线程处在同一个线程中
 * 这样处理的缺点就是如果某一个 Handler出现阻塞，那么整个 Reactor将会直接无法接收外部请求（同线程被阻塞）
 *
 * @author Doug Lea
 */
public class SingleReactor implements Runnable {

    final Selector selector;

    final ServerSocketChannel serverSocketChannel;

    public SingleReactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket()
                .bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey sk = serverSocketChannel
                .register(selector, SelectionKey.OP_ACCEPT);
        // 将处理自己相关事件的 handler放在附件中，
        // Acceptor也是一个 handler
        sk.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    dispatch(iterator.next());
                }
                keys.clear();
            }
        } catch (IOException e) {

        }
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        if (r != null) {
            r.run();
        }
    }

    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                     new Handler(selector, socketChannel);
                }
            } catch (IOException e) {

            }
        }
    }
}
