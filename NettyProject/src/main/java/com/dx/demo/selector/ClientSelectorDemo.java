/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static com.dx.io.NetConstants.HOST;
import static com.dx.io.NetConstants.SERVER_PORT;

/**
 * 使用 NIO 书写的客户端
 *
 * @author daixiao
 */
public class ClientSelectorDemo {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(ClientSelectorDemo.class);

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);

            // 如果链接不上就停止等待，不会造成阻塞
            if (!socketChannel.connect(new InetSocketAddress(HOST, SERVER_PORT))) {
                while (!socketChannel.finishConnect()) {
                    log.info("connect to server need server time");
                }
            }

            String greet = "Hello, World!~";
            ByteBuffer buffer = ByteBuffer.wrap(greet.getBytes(StandardCharsets.UTF_8));
            socketChannel.write(buffer);
            System.in.read();
        } catch (IOException e) {
            log.warn("create channel failed!", e);
        }
    }
}
