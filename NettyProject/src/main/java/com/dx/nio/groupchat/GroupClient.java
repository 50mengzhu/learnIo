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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static com.dx.nio.groupchat.GroupConstants.SERVER_HOST;
import static com.dx.nio.groupchat.GroupConstants.SERVER_PORT;

/**
 * 群聊系统的客户端程序
 *
 * @author daixiao
 */
public class GroupClient {

    /** 记录日志的对象 */
    private static Log log = LogFactory.getLog(GroupClient.class);

    /** 客户端使用的 Channel */
    private SocketChannel socketChannel;

    /** 获取从服务器中返回的Channel */
    private Selector selector;

    /** 超时时间 */
    public static final Integer TIMEOUT = 2000;

    /** 缓冲区大小 */
    public static final int BUFFER_SIZE = 1024;

    /** 线程池工厂 */
    public static ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("NIO-client-%d").build();

    /** 线程池对象 */
    private static ExecutorService executor = new ThreadPoolExecutor(5, 200, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), factory);

    /**
     * 初始化方法
     */
    public void init() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            log.warn("", e);
        }
    }

    public void readMsg() {
        try {
            if (selector.select(TIMEOUT) < 0) {
                log.info("no channel is working");
                return;
            }
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                    channel.read(byteBuffer);
                    log.info(new String(byteBuffer.array(), StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            log.warn("read data from server", e);
        }
    }

    public void sendInfo(String message) {
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.warn("write failed!!", e);
        }
    }

    public static void main(String[] args) {
        GroupClient groupClient = new GroupClient();
        executor.execute(new Thread(() -> {
            groupClient.init();
            while (true) {
                groupClient.readMsg();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.warn("", e);
                }
            }
        }));

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            groupClient.sendInfo(line);
        }
    }

}
