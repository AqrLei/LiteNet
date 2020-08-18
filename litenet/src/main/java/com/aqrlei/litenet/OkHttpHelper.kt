package com.aqrlei.litenet

import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.net.URLConnection
import java.net.URLEncoder

/**
 * created by AqrLei on 2020/4/11
 */
object OkHttpHelper {
    private val MEDIA_TYPE_BYTE_ARRAY = "application/octet-stream; charset=utf-8".toMediaType()
    private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    private var okHttpRequest: OkHttpRequest? = null
    fun init(okHttpRequest: OkHttpRequest) {
        OkHttpHelper.okHttpRequest = okHttpRequest
    }

    fun <T> get(url: String, resultType: Type, callback: IHttpCallback<T>?) =
        okHttpRequest?.get(url, resultType, callback)

    fun <T> download(
        url: String,
        resultType: Type,
        callback: OkHttpRequest.DownloadCallback<T>?,
        transformer: ITransformer<T>?) =
        okHttpRequest?.download(url, resultType, callback, transformer)

    fun <T> postForm(
        url: String,
        resultType: Type,
        paramMap: MutableMap<String, String>,
        callback: IHttpCallback<T>?) {
        okHttpRequest?.postFormParameter(url, paramMap, resultType, callback)
    }

    fun <T> postJson(url: String, resultType: Type, callback: IHttpCallback<T>, json: String) {
        okHttpRequest?.postString(
            url, json,
            MEDIA_TYPE_JSON, resultType, callback)
    }

    fun <T> postFile(
        url: String,
        resultType: Type,
        paramMap: MutableMap<String, Any>,
        callback: IHttpCallback<T>?) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        for ((key, value) in paramMap) {
            when (value) {
                is File -> requestBody.addFormDataPart(
                    key, value.name, value.asRequestBody(
                        guessMimeType(value.absolutePath)
                            .toMediaType()))
                is ByteArray -> requestBody.addFormDataPart(
                    key, key, value.toRequestBody(
                        MEDIA_TYPE_BYTE_ARRAY))
                else -> requestBody.addPart(
                    Headers.headersOf("Content-Disposition", "form-data; name=\"$key\""),
                    value.toString().toRequestBody())
            }
        }
        okHttpRequest?.postMultipart(url, requestBody.build(), resultType, callback)
    }

    private fun guessMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor: String? = null
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return contentTypeFor
    }
}