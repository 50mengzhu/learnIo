/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.model;

import java.nio.channels.SelectionKey;

/**
 * Reactor模式的一个抽象接口
 * 通过 Reactor接收请求，并对请求依照时间类型进行分发
 *
 * @author mica
 */
public interface Reactor {

    /**
     * 对来自客户端的请求进行分发处理
     *
     * @param key 有 I/O事件的 key
     */
    void dispatch(SelectionKey key);

    /**
     * 分发的时候采用多线程进行分发
     *
     * @param key   IO事件就绪的 key
     */
    void multiDispatch(SelectionKey key);
}
