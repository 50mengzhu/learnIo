/**
 * copyright@daixiao
 * file encoding: gbk
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
 * ����ʹ��һ�� Buffer ����������ݵĽ���
 * Ҳ���ǵ�һ�� Buffer ������ʱ����Բ��� Buffer ������ж�ȡ����߶�д�ٶ�
 *
 * @author daixiao
 */
public class BufferScattingAndGathering {

    /** ��־��¼���� */
    private static Log log = LogFactory.getLog(BufferScattingAndGathering.class);

    /** ��һ�� Buffer �Ĵ�С */
    private static final int FIRST_BUFFER_SIZE = 5;

    /** �ڶ��� Buffer �Ĵ�С */
    private static final int SECOND_BUFFER_SIZE = 3;

    public static void main(String[] args) {

        // ר�Ŵ���һ�� ServerSocketChannel ���ڰ󶨷������˵�����
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            log.info(String.format("server listen at port %d ...", SERVER_PORT));

            // ����һ�� SocketChannel ר�����ںͿͻ��˵�ͨ��
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.info(String.format("client %s connect!", socketChannel.getLocalAddress()));

            // ʹ��һ�� buffer ����Դ�������ݽ��н������ݴ�
            ByteBuffer[] buffers = new ByteBuffer[2];
            buffers[0] = ByteBuffer.allocate(FIRST_BUFFER_SIZE);
            buffers[1] = ByteBuffer.allocate(SECOND_BUFFER_SIZE);

            // ��С��ȡ����������� buffer �в����������С����ô�����ж�ȡ
            int msgMinLength = 8;

            while (true) {
                // �� buffer �ж�ȡ���ֽ�����
                int byteRead = 0;
                // �����ȡ��������С����С�Ŀɶ����ֽ���
                // ��ô�ͼ����ȴ��� Buffer ��д�������
                while (byteRead < msgMinLength) {
                    long read = socketChannel.read(buffers);
                    byteRead += read;
                }
                // ʹ�� Stream ��ʽ��ӡ�����е� buffer �� position �� limit
                Arrays.asList(buffers).stream().forEach(buffer -> {
                    log.info("position = " + buffer.position() + " , limit = " + buffer.limit());
                });

                // ʹ�� stream �ķ�ʽ��� buffer �ķ�ת
                Arrays.asList(buffers).forEach(buffer -> buffer.flip());

                // ��ͻ��˷�������
                int byteWrite = 0;
                while (byteWrite < msgMinLength) {
                    long write = socketChannel.write(buffers);
                    byteWrite += write;
                }

                // �����������е� buffer
                Arrays.asList(buffers).forEach(buffer -> buffer.clear());
            }
        } catch (IOException e) {
            log.warn("server socket channel open failed!", e);
        }
    }
}
