package com.fpliu.newton.im

import java.io.DataInputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * 这是对服务端返回数据的默认协议解析
 * @author 792793182@qq.com 2018-05-03.
 */
class DefaultResponseStreamReader : ResponseStreamReader<IMResponse> {

    @Throws(IOException::class)
    override fun readFromStream(im: IM<IMResponse>, dataInputStream: DataInputStream): IMResponse? {
        //服务端发过来的数据必须不小于17个字节。这是协议决定的。如果不够17个字节，就循环等待达到数量
        while (im.isRunning && dataInputStream.available() < 17);

        var available = dataInputStream.available()
        if (available < 17) {
            return null
        }
        //先读取4个字节，转换成整数
        val packageLength = dataInputStream.readInt()

        //剩余的字节数
        val otherBytes = packageLength - Integer.BYTES

        //流里面的数据应该不小于otherBytes个字节。如果不够otherBytes个字节，就循环等待达到数量
        while (im.isRunning && dataInputStream.available() < otherBytes);

        available = dataInputStream.available()
        if (available < otherBytes) {
            return null
        }

        val response = IMResponse().apply {
            this.packageLength = packageLength
            headLength = dataInputStream.readShort()
            version = dataInputStream.readShort()
            operation = dataInputStream.readInt()
            sequenceId = dataInputStream.readInt()
            compressed = dataInputStream.readUnsignedByte().toByte()
        }
        val payloadDataLength = response.packageLength - response.headLength
        if (payloadDataLength > 0) {
            var buffer = ByteArray(payloadDataLength)
            dataInputStream.read(buffer, 0, payloadDataLength)
            if (response.compressed.toInt() == 1) {
                buffer = IMUtil.unzip(buffer)
            }
            response.payloadData = String(buffer, Charset.forName("UTF-8"))
        }

        return response
    }
}
