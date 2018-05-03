package com.fpliu.newton.im

import java.io.DataInputStream
import java.io.IOException

/**
 * 从服务端返回的数据流中读取数据
 * 实现此接口可以自定义服务端返回数据的协议
 * @author 792793182@qq.com 2018-05-03.
 */
interface ResponseStreamReader<T : IMResponse> {
    @Throws(IOException::class)
    fun readFromStream(im: IM<T>, dataInputStream: DataInputStream): T?
}
