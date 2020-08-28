package com.aqrlei.litenet.request.coroutine

import com.aqrlei.litenet.IHttpCallback
import com.aqrlei.litenet.IHttpRequestController
import com.aqrlei.litenet.ITransformer
import com.aqrlei.litenet.exception.CancelException
import com.aqrlei.litenet.exception.HttpException
import kotlinx.coroutines.*
import okhttp3.Call
import java.lang.reflect.Type
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resumeWithException

class CoroutineRequestController<T> private constructor(
    private val callback: IHttpCallback<T>,
    private val transformer: ITransformer<T>,
    private val requestScope: CoroutineScope,
    private val executorService: ExecutorService? = null // 自定义线程池
) : IHttpRequestController<T> {

    companion object {
        fun <T> build(
            callback: IHttpCallback<T>,
            transformer: ITransformer<T>,
            requestScope: CoroutineScope?,
            executorService: ExecutorService?
        ): CoroutineRequestController<T> {
            return CoroutineRequestController(
                callback,
                transformer,
                requestScope ?: MainScope(),
                executorService
            )
        }
    }

    private var isCanceled = AtomicBoolean(false)
    private var job: Job? = null

    override fun execute(call: Call, resultType: Type): CoroutineRequestController<T> {
        callback.onBefore()
        job = coroutineCancellableRun(
            backgroundBlock = {
                val executeBlock = {
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
                }
                executorService?.asCoroutineDispatcher()?.use {
                    executeBlock.invoke()
                } ?: withContext(Dispatchers.IO) {
                    executeBlock.invoke()
                }
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

    private fun <T> coroutineCancellableRun(
        backgroundBlock: suspend () -> T,
        cancelBlock: () -> Unit,
        resultCallback: (result: T) -> Unit,
        errorCallback: ((e: Throwable) -> Unit)? = null
    ): Job {
        return requestScope.launch {
            try {
                resultCallback(awaitCancellable(backgroundBlock, cancelBlock))
            } catch (e: Exception) {
                errorCallback?.invoke(e)
            }
        }
    }

    private suspend fun <T> awaitCancellable(
        backgroundBlock: suspend () -> T,
        cancelBlock: () -> Unit
    ) =
        suspendCancellableCoroutine<T> { cancellableContinuation ->
            requestScope.launch {
                try {
                    cancellableContinuation.resumeWith(Result.success(backgroundBlock()))
                } catch (e: Exception) {
                    cancellableContinuation.resumeWithException(e)
                }
                cancellableContinuation.invokeOnCancellation {
                    try {
                        cancelBlock()
                    } catch (e: Throwable) {
                        throw e
                    }
                }
            }
        }
}