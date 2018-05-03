# IM
kotlin语言实现的一个简单的IM

## 引用
```
implementation("com.fpliu:IM:1.0.0")
```

## 使用(kotlin版)
```
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
```

## 自定义协议
可以通过实现<code>RequestPackageBuilder</code>接口来实现自己的发送给服务器的协议。
<br>
可以通过实现<code>ResponseStreamReader</code>接口来实现服务器返回的协议。

