package com.fpliu.newton.im

/**
 * IM相关的预定义操作
 * @author 792793182@qq.com 2018-05-03.
 */
object IMOperation {

    // 客户端发起握手协议
    const val OP_HANDSHAKE = 0

    // 服务端对客户端发起的握手协议的响应
    const val OP_HANDSHAKE_REPLY = 1

    // 客户端发送心跳协议
    const val OP_HEARTBEAT = 2

    // 服务端对客户端发送的心跳协议的响应
    const val OP_HEARTBEAT_REPLY = 3

    // 客户端发送普通消息协议
    const val OP_SEND_SMS = 4

    // 服务端发送普通消息协议
    const val OP_SEND_SMS_REPLY = 5

    //
    const val OP_DISCONNECT_REPLY = 6

    // 客户端发送用户认证协议
    const val OP_AUTH = 7

    //服务端对客户端发送的认证协议的响应
    const val OP_AUTH_REPLY = 8
}
