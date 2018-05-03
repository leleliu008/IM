package com.fpliu.newton.im

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.Inflater

/**
 * @author 792793182@qq.com 2018-05-03.
 */
object IMUtil {

    /**
     * 解压缩
     *
     * @param data 待压缩的数据
     * @return byte[] 解压缩后的数据
     */
    fun unzip(data: ByteArray): ByteArray {
        var output: ByteArray

        val inflater = Inflater()
        inflater.reset()
        inflater.setInput(data)

        val outputStream = ByteArrayOutputStream(data.size)
        try {
            val buf = ByteArray(1024)
            while (!inflater.finished()) {
                val i = inflater.inflate(buf)
                outputStream.write(buf, 0, i)
            }
            output = outputStream.toByteArray()
        } catch (e: Exception) {
            output = data
            e.printStackTrace()
        } finally {
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        inflater.end()
        return output
    }
}
