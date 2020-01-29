/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ʹ�� NIO �����ļ�֮�����ݵ��໥����
 * ʹ�� Buffer �����ļ����ݴ�
 *
 * @author daixiao
 */
public class NioCopyFileUsingBuffer {

    /** ��־��¼���� */
    private static Log log = LogFactory.getLog(NioCopyFileUsingBuffer.class);

    /** ÿ�ζ��ļ��Ļ������Ĵ�С */
    private static final int BUFFER_SIZE = 1024;

    /** �ļ���β */
    private static final int EOF = -1;

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream(new File("hello.txt"));
             FileOutputStream outputStream = new FileOutputStream(new File("helloCopy.txt"))) {
            FileChannel fileChannel = inputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                // ��ȡ֮ǰ��Ҫ�� buffer �е��ĸ����Խ��л�ԭ�Ĳ���
                // ����ִ�У�����ִ�����ȡ֮��position �� limit ���
                // ���صĶ�ȡ����ʼ��Ϊ0
                buffer.clear();
                // �� channel �е����ݶ��� buffer ��
                int read = fileChannel.read(buffer);
                if (read == EOF) {
                    break;
                }
                // ��ת buffer ����������ݵ�д��
                buffer.flip();
                FileChannel channel = outputStream.getChannel();
                channel.write(buffer);
            }
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("create file failed!", e);
        }
    }
}
