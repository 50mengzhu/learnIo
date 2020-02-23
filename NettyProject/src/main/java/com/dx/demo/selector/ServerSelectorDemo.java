/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.demo.selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import static com.dx.io.NetConstants.SERVER_PORT;
import static com.dx.io.NetConstants.TIMEOUT_THOUSAND;

/**
 * 使用 Selector 进行服务器端的多路复用
 *
 * @author daixiao
 */
public class ServerSelectorDemo {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(ServerSelectorDemo.class);

    public static void main(String[] args) {
        // 1. 首先创建一个服务端的 channel
        // 将服务器端 channel 绑定对应的端口，
        // 设置服务器端 channel 为非阻塞，并将其注册到对应的 selector 上

        // 2. 创建一个 选择器 selector 用于监听 channel 中的事件
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 3. 准备循环接收来自客户端的 连接
            while (true) {
                // 等待 1 ms 如果没有事件，那么就继续准备监听
                if (selector.select(TIMEOUT_THOUSAND) == 0) {
                    log.info(String.format("server has been waiting %dms", TIMEOUT_THOUSAND));
                    continue;
                }

                // 能进到这里说明已经监听到 channel 中存在事件
                // 获取所有的监听的事件，转换为迭代器类型便于遍历
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keySet.iterator();
                // 遍历所有的事件
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        // 通过 SelectionKey 和 channel 的对应关系完成处理
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    }
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        // 获取 key 中的 buffer
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        socketChannel.read(buffer);
                        log.info(new String(buffer.array(), StandardCharsets.UTF_8));
                        buffer.clear();
                    }

                    // 注意！完成之后需要将处理完成的 key 移除
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            log.warn("open server socket channel failed!", e);
        }
    }
}
