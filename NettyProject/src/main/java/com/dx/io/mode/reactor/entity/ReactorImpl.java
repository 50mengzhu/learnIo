/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.entity;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dx.io.mode.reactor.model.Handler;
import com.dx.io.mode.reactor.model.Reactor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Reactor 的一个实现类
 *
 * @author mica
 */
public class ReactorImpl implements Reactor {

    private static Handler[] handlers;

    private static Map<Integer, Handler> relations = new HashMap<>();


    private static ThreadFactory factory = new ThreadFactoryBuilder()
            .setNameFormat("thread-%d").build();

    private static ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024),
            factory, new ThreadPoolExecutor.AbortPolicy());

    static {
        handlers = new Handler[3];
        // TODO 可以优化的
        reg(new AcceptHandler());
        reg(new ReadHandler());
        reg(new WriteHandler());
    }

    private Handler getHandler(SelectionKey key) {
        int event = 0;
        if (key.isReadable()) {
            event = SelectionKey.OP_READ;
        }
        if (key.isWritable()) {
            event = SelectionKey.OP_WRITE;
        }
        if (key.isAcceptable()) {
            event = SelectionKey.OP_ACCEPT;
        }

        return relations.get(event);
    }

    @Override
    public void dispatch(SelectionKey key) {
        getHandler(key).handle(key);
    }

    @Override
    public void multiDispatch(SelectionKey key) {
        singleThreadPool.execute(() -> dispatch(key));
    }

    private static void reg(Handler handler) {
        int i = 0;
        for (; i < handlers.length; ++ i) {
            if (handlers[i] == handler) {
                return;
            }
            if (handlers[i] == null) {
                break;
            }
        }
        handlers[i] = handler;
        relations.put(handler.interestOps(), handler);
    }
}
