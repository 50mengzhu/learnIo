/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * ʹ�� NIO �ķ�ʽ���ļ���д������
 *
 * @author daixiao
 */
public class NioWriteFile {

    /** �����ļ���д����ַ��� */
    private static String str2File = "Hello world!";

    /** ��¼��־�Ķ��� */
    private static Log log = LogFactory.getLog(NioWriteFile.class);

    /**
     * ʵ������� FileChannel ��ʵ������ FileChannelImpl
     * @see {sun.nio.ch.FileChannelImpl}
     * @param args �����в���
     */
    public static void main(String[] args) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File("hello.txt"))) {
            // 1. ���ȴ���һ�������ļ��ȴ�����

            // 2. �����ϵ��ļ���ת��Ϊ channel
            // ʵ������� FileChannel ��ʵ������ FileChannelImpl
            FileChannel fileChannel = fileOutputStream.getChannel();

            // 3. ����һ������������������д�� buffer ��
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(str2File.getBytes(StandardCharsets.UTF_8));
            // ע����� buffer ��д���֮����Ҫ�� buffer ��תһ��
            buffer.flip();
            fileChannel.write(buffer);
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("channel read buffer failed!", e);
        }
    }
}
