package com.fpliu.newton.im

import java.util.concurrent.atomic.AtomicInteger

/**
 * 生成消息序列号的
 * @author 792793182@qq.com 2018-05-03.
 */
object IMSeqId {

    private val atomicInteger = AtomicInteger()

    fun nextId(): Int = atomicInteger.getAndIncrement()
}
