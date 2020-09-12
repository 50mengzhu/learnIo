/**
 * copyright@doug lea
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.demo.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 多线程版本的 Reactor，将 Handler中的处理方式采用了多线程
 * IO操作仍然是 Reactor中完成的
 * @author Doug Lea
 */
public class MultiThreadHandler extends Handler {

    static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();
    static ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    final static int PROCESSING = 3;

    public MultiThreadHandler(Selector selector, SocketChannel channel) throws IOException {
        super(selector, channel);
    }

    @Override
    synchronized void read() throws IOException {
        socketChannel.read(input);
        if (inputIsComplete()) {
            // 此处体现多线程后续非 I/O操作
            status = PROCESSING;
            singleThreadPool.execute(new Processor());
        }
    }


    synchronized void processAndHandleOff() {
        process();
        status = SENDING;
        key.interestOps(SelectionKey.OP_WRITE);
    }

    class Processor implements Runnable {
        @Override
        public void run() {
            processAndHandleOff();
        }
    }
}
