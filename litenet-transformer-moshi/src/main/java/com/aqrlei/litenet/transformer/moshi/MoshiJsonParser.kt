package com.aqrlei.litenet.transformer.moshi

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.BufferedSource
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/3/20
 */
class MoshiJsonParser private constructor(private val moshi: Moshi) {

    companion object {
        fun init(moshi: Moshi): MoshiJsonParser {
            return MoshiJsonParser(moshi)
        }
    }

    fun <T> toJson(data: T, type: Type): String {
        return moshi.adapter<T>(type).toJson(data)
    }

    fun <R> fromJson(bufferedSource: BufferedSource, resultType: Type): R? {
        return moshi.adapter<R>(resultType).fromJson(bufferedSource)
    }

    fun <R> fromJson(string: String, resultType: Type): R? {
        return moshi.adapter<R>(resultType).fromJson(string)
    }

    fun <R> fromJson(jsonReader: JsonReader, resultType: Type): R? {
        return moshi.adapter<R>(resultType).fromJson(jsonReader)
    }
}