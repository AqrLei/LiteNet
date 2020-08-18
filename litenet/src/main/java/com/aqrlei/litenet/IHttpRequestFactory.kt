package com.aqrlei.litenet

/**
 * created by AqrLei on 2020/4/21
 */
interface IHttpRequestFactory {
    fun <T> createHttpRequest(
        callback: IHttpCallback<T>,
        transformer: ITransformer<T>): IHttpRequestController<T>
}