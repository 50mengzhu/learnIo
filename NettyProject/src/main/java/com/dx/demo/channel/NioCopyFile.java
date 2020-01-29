/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * ʹ�� NIO �����ļ����໥����
 * ֱ��ʹ�� channel ֮��Ŀ���ͨ��
 *
 * @author daixiao
 */
public class NioCopyFile {

    /** ��־��¼���� */
    private static Log log = LogFactory.getLog(NioCopyFile.class);

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream("hello.txt");
             FileOutputStream outputStream = new FileOutputStream("helloCopy.txt")) {
            FileChannel srcChannel = inputStream.getChannel();
            // ���� transferFrom ����ֱ�ӿ���
            // �Ա� NioCopyFileUsingBuffer �࣬��������ͼ�������
            outputStream.getChannel().transferFrom(srcChannel, 0, srcChannel.size());
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("can not create file input stream", e);
        }
    }
}
