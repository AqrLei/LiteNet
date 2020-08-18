package com.aqrlei.litenet

import okhttp3.Call
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/4/21
 */
interface IHttpRequestController<T> {
    fun execute(call:Call, resultType: Type): IHttpRequestController<T>
    fun cancel()
}