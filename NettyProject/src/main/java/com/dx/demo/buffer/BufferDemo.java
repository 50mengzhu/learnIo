/**
 * copyright@daixiao
 * file encoding: gbk
 */
package com.dx.demo.buffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.IntBuffer;

/**
 * Buffer ��ʹ��ʵ��
 *
 * @author daixiao
 */
public class BufferDemo {

    /** ��¼��־�Ķ��� */
    private static Log log = LogFactory.getLog(BufferDemo.class);

    /** buffer �Ĵ�С */
    private static int SIZE = 5;

    public static void main(String[] args) {
        // ���ȷ��� SIZE ��С�� Buffer ��С
        IntBuffer intBuffer = IntBuffer.allocate(SIZE);

        for (int i = 0; i < intBuffer.capacity(); ++ i) {
            intBuffer.put(i * 2);
        }

        // ע���ڶ�дת����ʱ����Ҫ�����������
        intBuffer.flip();

        // �ж� intBuffer �Ƿ���ʣ�µ�Ԫ��
        while (intBuffer.hasRemaining()) {
            log.info(intBuffer.get());
        }
    }
}
