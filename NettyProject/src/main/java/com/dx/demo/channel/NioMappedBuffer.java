/**
 * copyright@daixiao
 * file encoding: utf-8
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
 * 使用 MappedByteBuffer 这个类对文件进行修改，在堆外内存进行修改
 * 操作系统不需要对文件进行拷贝
 *
 * @author daixiao
 */
public class NioMappedBuffer {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(NioMappedBuffer.class);

    public static void main(String[] args) {
        // 可以使用 RandomAccessFile 可以直接对文件进行修改
        // 配合该类中包含的 channel 进行修改
        try (RandomAccessFile file = new RandomAccessFile("hello.txt", "rw")) {
            FileChannel channel = file.getChannel();
            // 说明一下 channel.map 的三个参数的具体含义
            // 参数一：修改文件的模式，主要是读/写
            // 参数二：文件修改的起始位置，起始下标
            // 参数三：文件修改的字节数量
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

            // 将原本文件中的第一个字节替换为 J
            mappedByteBuffer.put(0, (byte) 'J');
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("map failed!", e);
        }
    }
}
