/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static com.dx.io.NetConstants.NET_BUFFER;
import static com.dx.io.NetConstants.SERVER_PORT;
import static com.dx.io.NetConstants.TIMEOUT;


/**
 * NIO 的 TCP 的服务端
 *
 * @author daixiao
 */
public class NioServer {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(NioServer.class);

    /** 创建线程的线程工厂 */
    private static ThreadFactory factory = new ThreadFactoryBuilder()
            .setNameFormat("nio-thread-pool-nio-%d").build();
    /** 创建线程池，使用线程池进行线程的创建 */
    private static ExecutorService pool = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), factory);

    /**
     * 服务端的线程，用于监听的 Selector
     */
    static class ServerSelector implements Runnable {

        /**
         * 构造方法
         * @param serverSelector 专门的服务器选择器
         * @param clientSelector 专门的客户端选择器
         */
        public ServerSelector(final Selector serverSelector, final Selector clientSelector) {
            this.serverSelector = serverSelector;
            this.clientSelector = clientSelector;
        }

        /** selector 服务端的一个轮询的 Selector */
        private Selector serverSelector;
        /** selector 客户端的一个轮询 Selector */
        private Selector clientSelector;

        @Override
        public void run() {
            try {
                ServerSocketChannel listenChannel = ServerSocketChannel.open();
                listenChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
                listenChannel.configureBlocking(false);
                listenChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true) {
                    if (serverSelector.select(TIMEOUT) > 0) {
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();

                            if (key.isAcceptable()) {
                                try {
                                    SocketChannel clientSocket = ((ServerSocketChannel) key.channel()).accept();
                                    clientSocket.configureBlocking(false);
                                    clientSocket.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.warn("serverSocket channel open failed!", e);
            }
        }
    }


    /**
     * 客户端的选择器
     * 用于处理专门的 IO 事件
     */
    static class WorkSelector implements Runnable {

        /** 客户端的选择器 */
        private Selector clientSelector;

        /**
         * 构造方法
         * @param clientSelector 客户端的选择器
         */
        WorkSelector (Selector clientSelector) {
            this.clientSelector = clientSelector;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (clientSelector.select(TIMEOUT) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isReadable()) {
                                try {
                                    SocketChannel channel = (SocketChannel) key.channel();
                                    ByteBuffer buf = ByteBuffer.allocate(NET_BUFFER);
                                    channel.read(buf);
                                    buf.flip();
                                    log.info(Charset.defaultCharset().newDecoder().decode(buf).toString());
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.warn("read channel failed!", e);
            }

        }
    }

    public static void main(String[] args) {
        try {
            final Selector serverSelector = Selector.open();
            final Selector clientSelector = Selector.open();

            pool.execute(new ServerSelector(serverSelector, clientSelector));
            pool.execute(new WorkSelector(clientSelector));
        } catch (IOException e) {
            log.warn("open selector failed!", e);
        }
    }
}
