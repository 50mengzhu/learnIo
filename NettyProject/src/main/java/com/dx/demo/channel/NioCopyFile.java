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
 * 使用 NIO 进行文件的相互拷贝
 * 直接使用 channel 之间的拷贝通道
 *
 * @author daixiao
 */
public class NioCopyFile {

    /** 日志记录对象 */
    private static Log log = LogFactory.getLog(NioCopyFile.class);

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream("hello.txt");
             FileOutputStream outputStream = new FileOutputStream("helloCopy.txt")) {
            FileChannel srcChannel = inputStream.getChannel();
            // 调用 transferFrom 方法直接拷贝
            // 对比 NioCopyFileUsingBuffer 类，这个方法就简洁了许多
            outputStream.getChannel().transferFrom(srcChannel, 0, srcChannel.size());
        } catch (FileNotFoundException e) {
            log.warn("file not found!", e);
        } catch (IOException e) {
            log.warn("can not create file input stream", e);
        }
    }
}
