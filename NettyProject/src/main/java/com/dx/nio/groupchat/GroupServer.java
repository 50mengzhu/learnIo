/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.nio.groupchat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static com.dx.nio.groupchat.GroupConstants.SERVER_PORT;

/**
 * 群聊系统的服务端
 *
 * @author daixiao
 */
public class GroupServer {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(GroupServer.class);

    /** 选择器 */
    private Selector selector;

    /** 服务器监听 Channel */
    private ServerSocketChannel serverSocketChannel;

    /** 监听 key 等待时长 */
    public static final Integer WAIT_TIMEOUT = 2000;

    /** buffer 缓存大小 */
    public static final Integer BUFFER_SIZE = 1024;

    /**
     * 初始化 selector 和 Channel
     */
    public void init() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("server channel is ready! waiting for connection ...");
        } catch (IOException e) {
            log.warn("initial server selector or serverSocketChannel failed!", e);
        }
    }

    /**
     * 处理来自 client 的请求
     */
    public void handleClientRequest() {
        while (true) {
            try {
                if (selector.select(WAIT_TIMEOUT) < 0) {
                    log.info(String.format("server has waited %d ms, no client connected!", WAIT_TIMEOUT));
                    continue;
                }

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        // ֧离线提醒
                        log.info(String.format("%s is online!", socketChannel.hashCode()));
                    }

                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                        try {
                            int read = channel.read(byteBuffer);

                            if (read > 0) {
                                StringBuffer sb = new StringBuffer();
                                sb.append(channel.getLocalAddress());
                                sb.append(": ");
                                sb.append(new String(byteBuffer.array(), StandardCharsets.UTF_8));
                                sendMsgToOthers(channel, sb.toString());
                            }
                        } catch (IOException e) {
                            log.warn("read failed", e);
                            log.info(String.format("%s is offline!", channel.hashCode()));
                            // 断开之后需要取消注册
                            key.cancel();
                            channel.close();
                        }

                    }

                    // 记得将 key 从 keySet 中移除，避免重复处理
                    keyIterator.remove();
                }
            } catch (IOException e) {
                log.warn("", e);
            }

        }
    }

    /**
     * 转发信息
     * @param channel 发送信息的 channel
     * @param message 待发送的信息
     */
    private void sendMsgToOthers(SocketChannel channel, String message) {
        for (SelectionKey key: selector.keys()) {
             SelectableChannel targetChannel = key.channel();

             if (targetChannel instanceof SocketChannel && targetChannel != channel) {
                 SocketChannel writeChannel =  (SocketChannel) targetChannel;
                 ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
                 try {
                     writeChannel.write(buffer);
                 } catch (IOException e) {
                     log.warn("write buffer to Channel failed!", e);
                 }
             }
        }
    }

    public static void main(String[] args) {
        GroupServer groupServer = new GroupServer();
        groupServer.init();
        groupServer.handleClientRequest();
    }
}
