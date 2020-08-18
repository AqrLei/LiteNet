package com.aqrlei.litenet

import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/3/19
 */
interface ITransformer<T> {
    fun transformer(response: ResponseBody,resultType:Type):T
}