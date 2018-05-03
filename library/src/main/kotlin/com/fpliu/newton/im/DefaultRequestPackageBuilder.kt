package com.fpliu.newton.im

import java.nio.ByteBuffer

/**
 * @author 792793182@qq.com 2018-05-03.
 */
class DefaultRequestPackageBuilder : RequestPackageBuilder {

    override fun build(operation: Int, body: ByteArray): ByteArray {
        val headerByteSize = (4 + 2 + 2 + 4 + 4).toShort()
        val packageLength = body.size + headerByteSize
        val pack = ByteArray(packageLength)

        val byteBuffer = ByteBuffer.wrap(pack)
        // package byte size
        byteBuffer.putInt(packageLength)
        // header byte size
        byteBuffer.putShort(headerByteSize)
        // version
        byteBuffer.putShort(1.toShort())
        // operation
        byteBuffer.putInt(operation)
        // sequenceId
        byteBuffer.putInt(IMSeqId.nextId())
        // body
        byteBuffer.put(body)

        return pack
    }
}
