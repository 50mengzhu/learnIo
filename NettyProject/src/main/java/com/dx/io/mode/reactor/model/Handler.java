/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.io.mode.reactor.model;

import java.nio.channels.SelectionKey;

/**
 * Reactor中真正处理 I/O时间的处理器
 *
 * @author mica
 */
public interface Handler {

    /**
     * 返回当前 Handler感兴趣的处理事件
     * @return  事件代码，参见{@link java.nio.channels.SelectionKey}事件常量定义
     */
    int interestOps();

    /**
     * 处理事件的过程
     * @param key   channel和 selector关联实体
     */
    void handle(SelectionKey key);
}
