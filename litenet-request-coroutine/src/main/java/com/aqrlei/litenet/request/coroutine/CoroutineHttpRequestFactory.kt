package com.aqrlei.litenet.request.coroutine

import com.aqrlei.litenet.IHttpCallback
import com.aqrlei.litenet.IHttpRequestController
import com.aqrlei.litenet.IHttpRequestFactory
import com.aqrlei.litenet.ITransformer

/**
 * created by AqrLei on 2020/3/20
 */
class CoroutineHttpRequestFactory private constructor() :
    IHttpRequestFactory {
    companion object {
        fun create() = CoroutineHttpRequestFactory()
    }

    override fun <T> createHttpRequest(
        callback: IHttpCallback<T>,
        transformer: ITransformer<T>): IHttpRequestController<T> {
        return CoroutineRequestController.build(callback, transformer)
    }

}