/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import com.dx.io.mode.reactor.entity.ReactorImpl;
import com.dx.io.mode.reactor.model.Reactor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 单一线程的 reactor模式
 * <p>
 * 这种 Reactor模式主要是，Reactor线程和 Handler线程均集中在一个线程内.
 * 那么这种模式的<strong>缺点</strong>就是，如果当一个 handler在处理 I/O事件的时候发生了阻塞
 * 那么所有的 handler都会阻塞，甚至也不能在立即响应客户端的请求了（因为 Accept也是一个 handler）
 *
 * @author mica
 */
public class SingleReactorThread {

    public static final Integer TIMEOUT = 2 * 1000;

    private static Log log = LogFactory.getLog(SingleReactorThread.class);

    private Selector selector;

    private ServerSocketChannel channel;

    private Reactor reactor;

    private void createChannel() {
        try {
            selector = Selector.open();
            channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(8888));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            if (reactor == null) {
                reactor = new ReactorImpl();
            }
            log.info("waiting for connect ...");
        } catch (IOException ioe) {
            log.error("", ioe);
        }
    }

    private void listenAndWaiting() {
        while (true) {
            try {
                if (selector.select(TIMEOUT) < 0) {
                    log.debug("no one!");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    log.info("start to handle event!");
                    // 检查到事件发生就发送给 Reactor进行处理
                    // 如果是单线程的 Reactor则使用以下的代码
                    // reactor.dispatch(key);
                    // 如果是多线程的 Reactor则使用以下的代码
                    reactor.multiDispatch(key);
                    log.info("finish!");
                    iterator.remove();
                }
            } catch (IOException ioe) {
                log.error("", ioe);
            }
        }
    }

    public void start() {
        createChannel();
        listenAndWaiting();
    }

    public static void main(String[] args) {
        new SingleReactorThread().start();
    }
}
