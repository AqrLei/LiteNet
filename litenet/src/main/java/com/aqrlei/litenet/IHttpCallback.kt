package com.aqrlei.litenet

import okhttp3.Response

/**
 * created by AqrLei on 2020/3/19
 */
interface IHttpCallback<T> {
    fun onBefore()

    fun onBackground(response: Response): T?

    fun onSuccess(data: T)

    fun onFailure(e: Throwable)

    fun onCancel(e: Throwable?)

    fun onFinish(data: T?, e: Throwable?)
}