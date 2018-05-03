package com.fpliu.newton.im

/**
 * 服务端返回的协议包的对象映射
 * @author 792793182@qq.com 2018-05-03.
 */
open class IMResponse {
    var packageLength: Int = 0
    var headLength: Short = 0
    var sequenceId: Int = 0
    var version: Short = 0
    var operation: Int = 0
    var compressed: Byte = 0
    var payloadData: String? = null

    override fun toString(): String {
        return "IMResponse(packageLength=$packageLength, headLength=$headLength, sequenceId=$sequenceId, version=$version, operation=$operation, payloadData=$payloadData, compressed=$compressed)"
    }
}