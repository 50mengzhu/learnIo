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
 * ʹ�� Selector ���з������˵Ķ�·����
 *
 * @author daixiao
 */
public class ServerSelectorDemo {

    /** ��־��¼���� */
    private static Log log = LogFactory.getLog(ServerSelectorDemo.class);

    public static void main(String[] args) {
        // 1. ���ȴ���һ������˵� channel
        // ���������� channel �󶨶�Ӧ�Ķ˿ڣ�
        // ���÷������� channel Ϊ��������������ע�ᵽ��Ӧ�� selector ��

        // 2. ����һ�� ѡ���� selector ���ڼ��� channel �е��¼�
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 3. ׼��ѭ���������Կͻ��˵� ����
            while (true) {
                // �ȴ� 1 ms ���û���¼�����ô�ͼ���׼������
                if (selector.select(TIMEOUT_THOUSAND) == 0) {
                    log.info(String.format("server has been waiting %dms", TIMEOUT_THOUSAND));
                    continue;
                }

                // �ܽ�������˵���Ѿ������� channel �д����¼�
                // ��ȡ���еļ������¼���ת��Ϊ���������ͱ��ڱ���
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keySet.iterator();
                // �������е��¼�
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        // ͨ�� SelectionKey �� channel �Ķ�Ӧ��ϵ��ɴ���
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        // ��ȡ key �е� buffer
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        socketChannel.read(buffer);
                        log.info(new String(buffer.array(), StandardCharsets.UTF_8));
                        buffer.clear();
                    }

                    // ע�⣡���֮����Ҫ��������ɵ� key �Ƴ�
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            log.warn("open server socket channel failed!", e);
        }
    }
}
