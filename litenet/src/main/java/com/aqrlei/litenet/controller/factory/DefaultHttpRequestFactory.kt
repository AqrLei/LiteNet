package com.aqrlei.litenet.controller.factory

import com.aqrlei.litenet.IHttpCallback
import com.aqrlei.litenet.IHttpRequestController
import com.aqrlei.litenet.IHttpRequestFactory
import com.aqrlei.litenet.ITransformer
import com.aqrlei.litenet.controller.CoroutineRequestController

/**
 * created by AqrLei on 2020/3/20
 */
class DefaultHttpRequestFactory private constructor() :
    IHttpRequestFactory {
    companion object {
        fun create() = DefaultHttpRequestFactory()
    }

    override fun <T> createHttpRequest(
        callback: IHttpCallback<T>,
        transformer: ITransformer<T>): IHttpRequestController<T> {
        return CoroutineRequestController.build(callback, transformer)
    }

}