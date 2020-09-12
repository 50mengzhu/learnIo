/**
 * copyright@doug lea
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.demo.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author Doug Lea
 */
public class Handler implements Runnable {

    public static final Integer MAX_IN = 1024;

    public static final Integer MAX_OUT = 1024;

    final SocketChannel socketChannel;

    final SelectionKey key;

    ByteBuffer input = ByteBuffer.allocate(MAX_IN);
    ByteBuffer output = ByteBuffer.allocate(MAX_OUT);

    static final int READING = 0, SENDING = 1;
    int status = READING;

    public Handler(Selector selector, SocketChannel channel) throws IOException {
        socketChannel = channel;
        channel.configureBlocking(false);
        // 因为 SelectableChannel#register不会重新注册，而是更新原始的信息
        // 此处相当于是将原始的 key中的感兴趣的事件全部清空
        key = socketChannel.register(selector, 0);
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    boolean inputIsComplete() {
        return true;
    }

    boolean outputIsComplete() {
        return true;
    }

    void process() {

    }

    @Override
    public void run() {
        try {
            if (status == READING) {
                read();
            } else if (status == SENDING) {
                send();
            }
        } catch (IOException e) {

        }
    }

    void read() throws IOException {
        // 阻塞读
        socketChannel.read(input);
        if (inputIsComplete()) {
            process();
            status = SENDING;
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        socketChannel.write(output);
        if (outputIsComplete()) {
            key.cancel();
        }
    }
}
