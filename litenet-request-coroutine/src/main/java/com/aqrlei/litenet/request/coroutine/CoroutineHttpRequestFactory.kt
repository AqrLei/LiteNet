package com.aqrlei.litenet.request.coroutine

import com.aqrlei.litenet.IHttpCallback
import com.aqrlei.litenet.IHttpRequestController
import com.aqrlei.litenet.IHttpRequestFactory
import com.aqrlei.litenet.ITransformer
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ExecutorService

/**
 * created by AqrLei on 2020/3/20
 */
class CoroutineHttpRequestFactory private constructor() : IHttpRequestFactory {
    companion object {
        fun create() = CoroutineHttpRequestFactory()
    }

    private var coroutineScope: CoroutineScope? = null
    private var executorService: ExecutorService? = null

    fun setScope(coroutineScope: CoroutineScope): CoroutineHttpRequestFactory {
        this.coroutineScope = coroutineScope
        return this
    }
    fun setExecutorService(executorService: ExecutorService) :CoroutineHttpRequestFactory {
        this.executorService = executorService
        return this
    }
    override fun <T> createHttpRequest(
        callback: IHttpCallback<T>,
        transformer: ITransformer<T>): IHttpRequestController<T> {
        return CoroutineRequestController.build(callback, transformer, coroutineScope, executorService)
    }

}