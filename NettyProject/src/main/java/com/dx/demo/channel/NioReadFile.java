/**
 * copyright@daixiao
 * file encoding: utf-8
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
 * 使用 NIO Channel 在文件中读取数据
 *
 * @author daixiao
 */
public class NioReadFile {

    /** 记录日志的对象 */
    private static Log log = LogFactory.getLog(NioReadFile.class);

    public static void main(String[] args) {
        // 1. 获取已经存在的一个文件
        try (FileInputStream inputStream = new FileInputStream(new File("hello.txt"))) {
            // 获取文件中的 channel
            FileChannel fileChannel = inputStream.getChannel();
            // 准备缓冲区 buffer ， 并向其中读入数据
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
