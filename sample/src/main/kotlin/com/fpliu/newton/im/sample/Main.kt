package com.fpliu.newton.im.sample

import com.fpliu.newton.im.*

fun main(args: Array<String>) {
    IM<IMResponse>().apply {
        host = ""
        port = 8080
        heartBeatInterval = 10 * 1000
        token = ""
        requestPackageBuilder = DefaultRequestPackageBuilder()
        responseStreamReader = DefaultResponseStreamReader()
        log = { tag, message ->
            //TODO
        }
        start({
            when (it.operation) {
                IMOperation.OP_AUTH_REPLY -> {
                    //TODO
                }
                IMOperation.OP_HEARTBEAT_REPLY -> {
                    //TODO
                }
                IMOperation.OP_SEND_SMS_REPLY -> {
                    //TODO
                }
            }
        })
    }
}
