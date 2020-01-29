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
 * 使用 NIO 进行文件之间内容的相互拷贝
 * 使用 Buffer 进行文件的暂存
 *
 * @author daixiao
 */
public class NioCopyFileUsingBuffer {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(NioCopyFileUsingBuffer.class);

    /** 每次读文件的缓冲区的大小 */
    private static final int BUFFER_SIZE = 1024;

    /** 文件结尾 */
    private static final int EOF = -1;

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream(new File("hello.txt"));
             FileOutputStream outputStream = new FileOutputStream(new File("helloCopy.txt"))) {
            FileChannel fileChannel = inputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                // 读取之前需要将 buffer 中的四个属性进行还原的操作
                // 必须执行，否则当执行完读取之后，position 和 limit 相等
                // 返回的读取数量始终为0
                buffer.clear();
                // 将 channel 中的数据读到 buffer 中
                int read = fileChannel.read(buffer);
                if (read == EOF) {
                    break;
                }
                // 反转 buffer 用于完成数据的写入
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
