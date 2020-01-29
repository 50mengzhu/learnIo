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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * ʹ�� NIO Channel ���ļ��ж�ȡ����
 *
 * @author daixiao
 */
public class NioReadFile {

    /** ��¼��־�Ķ��� */
    private static Log log = LogFactory.getLog(NioReadFile.class);

    public static void main(String[] args) {
        // 1. ��ȡ�Ѿ����ڵ�һ���ļ�
        try (FileInputStream inputStream = new FileInputStream(new File("hello.txt"))) {
            // ��ȡ�ļ��е� channel
            FileChannel fileChannel = inputStream.getChannel();
            // ׼�������� buffer �� �������ж�������
            ByteBuffer buffer = ByteBuffer.allocate((int) (new File("hello.txt").length()));
            fileChannel.read(buffer);
            log.warn(new String(buffer.array(), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("file channel read failed!", e);
        }
    }
}
