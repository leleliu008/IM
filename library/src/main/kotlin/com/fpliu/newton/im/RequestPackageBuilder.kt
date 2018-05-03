package com.fpliu.newton.im

/**
 * 对协议进行打包
 * 实现此接口可以自定义传给服务端的协议
 * @author 792793182@qq.com 2018-05-03.
 */
interface RequestPackageBuilder {
    /**
     * 组装包
     * @operation {@see IMOperation}
     * @body
     */
    fun build(operation: Int, body: ByteArray): ByteArray
}
