/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.demo.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import static com.dx.io.NetConstants.SERVER_PORT;

/**
 * 可以使用一个 Buffer 数组进行数据的接收
 * 也就是当一个 Buffer 不够的时候可以采用 Buffer 数组进行读取以提高读写速度
 *
 * @author daixiao
 */
public class BufferScattingAndGathering {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(BufferScattingAndGathering.class);

    /** 第一个 Buffer 的大小 */
    private static final int FIRST_BUFFER_SIZE = 5;

    /** 第二个 Buffer 的大小 */
    private static final int SECOND_BUFFER_SIZE = 3;

    public static void main(String[] args) {

        // 专门创建一个 ServerSocketChannel 用于绑定服务器端的数据
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            log.info(String.format("server listen at port %d ...", SERVER_PORT));

            // 创建一个 SocketChannel 专门用于和客户端的通信
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.info(String.format("client %s connect!", socketChannel.getLocalAddress()));

            // 使用一个 buffer 数组对传输的数据进行接收与暂存
            ByteBuffer[] buffers = new ByteBuffer[2];
            buffers[0] = ByteBuffer.allocate(FIRST_BUFFER_SIZE);
            buffers[1] = ByteBuffer.allocate(SECOND_BUFFER_SIZE);

            // 最小读取的数量，如果 buffer 中不超过这个大小，那么不进行读取
            int msgMinLength = 8;

            while (true) {
                // 从 buffer 中读取的字节数量
                int byteRead = 0;
                // 如果读取到的数据小于最小的可读的字节数
                // 那么就继续等待向 Buffer 中写入的数据
                while (byteRead < msgMinLength) {
                    long read = socketChannel.read(buffers);
                    byteRead += read;
                }
                // 使用 Stream 方式打印出所有的 buffer 的 position 和 limit
                Arrays.asList(buffers).stream().forEach(buffer -> {
                    log.info("position = " + buffer.position() + " , limit = " + buffer.limit());
                });

                // 使用 stream 的方式完成 buffer 的反转
                Arrays.asList(buffers).forEach(buffer -> buffer.flip());

                // 向客户端发送数据
                int byteWrite = 0;
                while (byteWrite < msgMinLength) {
                    long write = socketChannel.write(buffers);
                    byteWrite += write;
                }

                // 依次重置所有的 buffer
                Arrays.asList(buffers).forEach(buffer -> buffer.clear());
            }
        } catch (IOException e) {
            log.warn("server socket channel open failed!", e);
        }
    }
}
