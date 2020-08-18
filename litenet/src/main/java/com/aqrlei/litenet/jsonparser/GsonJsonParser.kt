package com.aqrlei.litenet.jsonparser

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.lang.Exception
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/3/20
 */
//TODO
class GsonJsonParser{
    val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .serializeSpecialFloatingPointValues()
        .disableHtmlEscaping()
        .create()

    fun toJson(any:Any?):String?{
           return  gson.toJson(any?:return null)
    }

    fun <T>fromJson(json:String,resultType:Type):T?{
        return gson.fromJson<T>(json,resultType)
    }


}