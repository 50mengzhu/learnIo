/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ʹ�� MappedByteBuffer �������ļ������޸ģ��ڶ����ڴ�����޸�
 * ����ϵͳ����Ҫ���ļ����п���
 *
 * @author daixiao
 */
public class NioMappedBuffer {

    /** ��־��¼���� */
    private static Log log = LogFactory.getLog(NioMappedBuffer.class);

    public static void main(String[] args) {
        // ����ʹ�� RandomAccessFile ����ֱ�Ӷ��ļ������޸�
        // ��ϸ����а����� channel �����޸�
        try (RandomAccessFile file = new RandomAccessFile("hello.txt", "rw")) {
            FileChannel channel = file.getChannel();
            // ˵��һ�� channel.map �����������ľ��庬��
            // ����һ���޸��ļ���ģʽ����Ҫ�Ƕ�/д
            // ���������ļ��޸ĵ���ʼλ�ã���ʼ�±�
            // ���������ļ��޸ĵ��ֽ�����
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

            // ��ԭ���ļ��еĵ�һ���ֽ��滻Ϊ J
            mappedByteBuffer.put(0, (byte) 'J');
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("map failed!", e);
        }
    }
}
