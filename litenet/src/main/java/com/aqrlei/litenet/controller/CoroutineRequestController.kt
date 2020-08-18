package com.aqrlei.litenet.controller

import com.aqrlei.utilcollection.ext.coroutineCancellableRun
import com.aqrlei.litenet.IHttpCallback
import com.aqrlei.litenet.IHttpRequestController
import com.aqrlei.litenet.ITransformer
import com.aqrlei.litenet.exception.CancelException
import com.aqrlei.litenet.exception.HttpException
import kotlinx.coroutines.Job
import okhttp3.Call
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class CoroutineRequestController<T> private constructor(
    private val callback: IHttpCallback<T>,
    val transformer: ITransformer<T>) : IHttpRequestController<T> {
    companion object {
        fun <T> build(
            callback: IHttpCallback<T>,
            transformer: ITransformer<T>): CoroutineRequestController<T> {
            return CoroutineRequestController(callback, transformer)
        }
    }

    private var isCanceled = AtomicBoolean(false)
    private var job: Job? = null

    override fun execute(call: Call, resultType: Type): CoroutineRequestController<T> {
        callback.onBefore()
        job = coroutineCancellableRun(
            backgroundBlock = {
                var result: T? = null
                // 这里不是在主线程，就不再使用 call.enqueue了
                @Suppress("BlockingMethodInNonBlockingContext")
                call.execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.let {
                            val tempResult = callback.onBackground(response)
                            result = tempResult
                                ?: transformer.transformer(it, resultType)
                        } ?: throw NullPointerException("Response body is null. $response")
                    } else {
                        throw HttpException(response)
                    }
                }
                result ?: throw NullPointerException("Result is null. ")
            },
            resultCallback = { result ->
                callback.onSuccess(result)
                callback.onFinish(result, null)
            },
            cancelBlock = {
                call.cancel()
                throw CancelException()
            },
            errorCallback = { e ->
                if (isCanceled.get() && e is CancelException) {
                    callback.onCancel(e)
                } else {
                    callback.onFailure(e)
                }
                callback.onFinish(null, e)
            })
        return this
    }

    override fun cancel() {
        isCanceled.set(true)
        job?.cancel()
        callback.onCancel(null)
    }
}