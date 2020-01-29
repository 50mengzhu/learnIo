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
 * 使用 NIO 的方式向文件中写入数据
 *
 * @author daixiao
 */
public class NioWriteFile {

    /** 待向文件中写入的字符串 */
    private static String str2File = "Hello world!";

    /** 记录日志的对象 */
    private static Log log = LogFactory.getLog(NioWriteFile.class);

    /**
     * 实际上这个 FileChannel 真实类型是 FileChannelImpl
     * @see {sun.nio.ch.FileChannelImpl}
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File("hello.txt"))) {
            // 1. 首先创建一个关于文件等待输入

            // 2. 将以上的文件流转换为 channel
            // 实际上这个 FileChannel 真实类型是 FileChannelImpl
            FileChannel fileChannel = fileOutputStream.getChannel();

            // 3. 创建一个缓冲区，并把数据写入 buffer 中
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(str2File.getBytes(StandardCharsets.UTF_8));
            // 注意对于 buffer 读写完成之后需要将 buffer 反转一下
            buffer.flip();
            fileChannel.write(buffer);
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("channel read buffer failed!", e);
        }
    }
}
