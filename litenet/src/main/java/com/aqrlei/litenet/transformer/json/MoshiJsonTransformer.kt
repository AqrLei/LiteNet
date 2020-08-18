package com.aqrlei.litenet.transformer.json

import com.aqrlei.litenet.jsonparser.MoshiJsonParser
import com.aqrlei.litenet.transformer.AbstractTransformer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/3/20
 */
class MoshiJsonTransformer<T> private constructor() :
    AbstractTransformer<T>() {
    companion object {
        val moshiJsonParser =
            MoshiJsonParser.init(Moshi.Builder().add(KotlinJsonAdapterFactory()).build())

        fun <T> createJsonParser() =
            MoshiJsonTransformer<T>()
    }

    override fun transformer(response: ResponseBody, resultType: Type): T {
        log("transformer - ${Thread.currentThread()}")
        return moshiJsonParser.fromJson(response.source(), resultType)
            ?: throw NullPointerException("From json is null.")
    }
}