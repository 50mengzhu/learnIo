/**
 * copyright@doug lea
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.demo.reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.dx.io.mode.reactor.demo.handler.Handler;

/**
 * 多线程版本的 Reactor，因为 Reactor中进行的主要是 I/O事件
 * 在大量客户端涌入时可能出现扛不住的情况，因此分为两个 Reactor进行处理
 *
 * @author Doug Lea
 */
public class MultiThreadReactor extends SingleReactor {

    Selector[] selectors;

    int next = 0;

    public MultiThreadReactor(int port) throws IOException {
        super(port);
    }


    class Acceptor implements Runnable {

        @Override
        public synchronized void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    // 多线程版本的 Handler
                    // new MultiThreadHandler(selectors[next], socketChannel);
                    new Handler(selectors[next], socketChannel);
                }
                if (++next == selectors.length) {
                    next = 0;
                }
            } catch (IOException e) {

            }
        }
    }
}
