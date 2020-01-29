/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.buffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.IntBuffer;

/**
 * Buffer 的使用实例
 *
 * @author daixiao
 */
public class BufferDemo {

    /** 记录日志的对象 */
    private static Log log = LogFactory.getLog(BufferDemo.class);

    /** buffer 的大小 */
    private static int SIZE = 5;

    public static void main(String[] args) {
        // 首先分配 SIZE 大小的 Buffer 大小
        IntBuffer intBuffer = IntBuffer.allocate(SIZE);

        for (int i = 0; i < intBuffer.capacity(); ++ i) {
            intBuffer.put(i * 2);
        }

        // 注意在读写转换的时候需要调用这个方法
        intBuffer.flip();

        // 判断 intBuffer 是否含有剩下的元素
        while (intBuffer.hasRemaining()) {
            log.info(intBuffer.get());
        }
    }
}
