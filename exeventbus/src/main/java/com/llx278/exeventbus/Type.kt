package com.llx278.exeventbus

enum class Type {
    /**
     * 默认的订阅类型，发布事件的线程与执行事件的线程互不影响
     */
    DEFAULT,
    /**
     * 订阅一个阻塞的时间，发布事件的线程将会被阻塞，知道执行返回完毕
     */
    BLOCK_RETURN
}