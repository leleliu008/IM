package com.fpliu.newton.im

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * 直播中的发消息模块
 *
 * @author 792793182@qq.com 2017-06-12.
 */
class IM<T : IMResponse> {

    var log: ((tag: String, message: String) -> Unit)? = null

    var requestPackageBuilder: RequestPackageBuilder? = null

    var responseStreamReader: ResponseStreamReader<T>? = null

    var host: String? = null

    var port = 80

    /**
     * 心跳时间间隔（单位：毫秒， 默认是5s）
     */
    var heartBeatInterval: Long = 5000

    /**
     * Socket超时（单位：毫秒， 默认是3分钟）
     */
    var socketTimeOut = 3 * 60 * 1000

    /**
     * 从服务端获得的令牌
     */
    var token: String? = null

    val isRunning: Boolean
        get() = currentState.get() == State.RUNNING

    val isStopped: Boolean
        get() = currentState.get() == State.STOPPED

    private enum class State {
        STOPPED, STOPPING, RUNNING
    }

    private val currentState = AtomicReference(State.STOPPED)

    private val tag = IM::class.java.simpleName

    private val charset = Charset.forName("UTF-8")

    private val out = AtomicReference<DataOutputStream>()
    private val `in` = AtomicReference<DataInputStream>()

    private var heartBeatDisposable: Disposable? = null

    private var disposable: Disposable? = null

    private var onResponse: ((response: T) -> Unit)? = null


    fun start(onResponse: (response: T) -> Unit) {
        this.onResponse = onResponse
        disposable = Observable
                .create(ObservableOnSubscribe<T> { emitter ->
                    log?.invoke(tag, "start()")

                    val socket = Socket(InetAddress.getByName(host), port).apply {
                        tcpNoDelay = true
                        soTimeout = socketTimeOut
                    }

                    out.set(DataOutputStream(socket.getOutputStream()))
                    `in`.set(DataInputStream(socket.getInputStream()))

                    //将状态从STOPPED改为RUNNING
                    if (!currentState.compareAndSet(State.STOPPED, State.RUNNING)) {
                        log?.invoke(tag, "change state from STOPPED to RUNNING failed!")
                        return@ObservableOnSubscribe
                    }

                    sendAuth(token!!)

                    while (currentState.get() == State.RUNNING) {
                        try {
                            val response = responseStreamReader!!.readFromStream(this@IM, `in`.get()) ?: continue
                            log?.invoke(tag, "response = $response")

                            if (!emitter.isDisposed) {
                                emitter.onNext(response)

                                if (IMOperation.OP_AUTH_REPLY === response.operation) {
                                    //启动心跳
                                    startSendHeartBeat(emitter)
                                }
                            }
                        } catch (e: Exception) {
                            if (!emitter.isDisposed) {
                                emitter.onError(e)
                            }
                        }
                    }
                    log?.invoke(tag, "stopped")
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(onResponse::invoke, {
                    log?.invoke(tag, it.stackTrace.contentToString())
                    //停止旧的
                    stop()
                    //重新启动
                    start(onResponse)
                })
    }

    fun stop(): Boolean {
        log?.invoke(tag, "stop()")

        //这个观察者不再接收消息了，释放资源
        disposable?.dispose()
        disposable = null

        heartBeatDisposable?.dispose()
        heartBeatDisposable = null

        return if (currentState.compareAndSet(State.RUNNING, State.STOPPING)) {
            try {
                `in`.get().close()
                currentState.set(State.STOPPED)
                true
            } catch (e: IOException) {
                currentState.set(State.STOPPED)
                false
            }
        } else {
            currentState.set(State.STOPPED)
            false
        }
    }

    /**
     * 发送消息
     *
     * @param content 消息内容
     * @return 是否发送成功
     */
    fun sendMessage(content: String): Boolean {
        return sendPackage(IMOperation.OP_SEND_SMS, content.toByteArray(charset))
    }

    /**
     * 发送身份认证协议
     *
     * @param token 唯一的token，从业务服务器获得
     * @return 是否发送成功
     */
    fun sendAuth(token: String): Boolean {
        return sendPackage(IMOperation.OP_AUTH, token.toByteArray(charset))
    }

    /**
     * 发送心跳协议
     *
     * @param token 唯一的token，从业务服务器获得
     * @return 是否发送成功
     */
    fun sendHeartBeat(token: String): Boolean {
        return sendPackage(IMOperation.OP_HEARTBEAT, token.toByteArray(charset))
    }

    /**
     * 发送任意的协议
     *
     * @param operation 区分不同的协议
     * @param payloadData 额外的内容
     * @return 是否发送成功
     */
    @Synchronized
    fun sendPackage(operation: Int, payloadData: ByteArray): Boolean {
        log?.invoke(tag, "sendPackage() operation = $operation")
        return try {
            out.get().write(requestPackageBuilder!!.build(operation, payloadData))
            out.get().flush()
            true
        } catch (e: Exception) {
            stop()
            start(onResponse!!)
            false
        }
    }

    private fun startSendHeartBeat(emitter: ObservableEmitter<*>) {
        heartBeatDisposable = Observable
                .interval(heartBeatInterval, TimeUnit.MILLISECONDS)
                .map { sendHeartBeat(token ?: "") }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    log?.invoke(tag, "sendHeartBeatSuccess")
                }, {
                    log?.invoke(tag, "sendHeartBeatFail")
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                })
    }
}