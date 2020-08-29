package com.aqrlei.litenet

import android.net.Uri
import android.util.Log
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/3/19
 */
class OkHttpRequest private constructor(
    private val okHttpClient: OkHttpClient,
    private val transformerFactory: ITransformerFactory,
    private val httpRequestFactory: IHttpRequestFactory) {

    private val namesAndValues: MutableList<String> = ArrayList(20)

    fun addHeaders(name: String, value: String): OkHttpRequest {
        namesAndValues.add(name)
        namesAndValues.add(value)
        return this
    }

    fun setHeaders(namesAndValues: ArrayList<String>?) {
        namesAndValues ?: return
        clearHeaders()
        this.namesAndValues.addAll(namesAndValues)
    }

    fun setHeaders(vararg namesAndValues: String) {
        clearHeaders()
        this.namesAndValues.addAll(namesAndValues)
    }

    fun clearHeaders() {
        if (namesAndValues.isEmpty()) return
        namesAndValues.clear()
    }

    fun <T> download(
        url: String,
        resultType: Type,
        downloadCallback: DownloadCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {
        val request = Request.Builder().url(url).build()
        return httpRequestFactory
            .createHttpRequest(
                downloadCallback ?: DownloadCallback(),
                transformer ?: transformerFactory.createTransformer())
            .execute((specialClient ?: okHttpClient).newCall(request), resultType)
    }

    fun <T> get(
        url: String,
        resultType: Type,
        callback: IHttpCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {

        val request = Request.Builder()
            .headers(Headers.headersOf(*namesAndValues.toTypedArray()))
            .get().url(url).build()
        return httpRequestFactory
            .createHttpRequest(
                callback ?: SimpleHttpCallback(),
                transformer ?: transformerFactory.createTransformer())
            .execute((specialClient ?: okHttpClient).newCall(request), resultType)
    }

    fun <T> postString(
        url: String, postString: String, mediaType: MediaType,
        resultType: Type,
        callback: IHttpCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {
        return post(
            url,
            postString.toRequestBody(mediaType),
            resultType,
            callback,
            transformer,
            specialClient)
    }

    fun <T> postFile(
        url: String,
        postFile: File,
        mediaType: MediaType,
        resultType: Type,
        callback: IHttpCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {
        return post(
            url,
            postFile.asRequestBody(mediaType),
            resultType,
            callback,
            transformer,
            specialClient)
    }

    fun <T> postFormParameter(
        url: String,
        formParameterMap: MutableMap<String, String>,
        resultType: Type,
        callback: IHttpCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {
        val formBodyBuilder = FormBody.Builder()
        for ((key, value) in formParameterMap) {
            formBodyBuilder.add(key, value)
        }
        return post(
            url,
            formBodyBuilder.build(),
            resultType,
            callback,
            transformer,
            specialClient)
    }

    fun <T> postMultipart(
        url: String,
        multipart: MultipartBody,
        resultType: Type,
        callback: IHttpCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {
        return post(url, multipart, resultType, callback, transformer, specialClient)
    }

    fun <T> post(
        url: String, requestBody: RequestBody,
        resultType: Type,
        callback: IHttpCallback<T>? = null,
        transformer: ITransformer<T>? = null,
        specialClient: OkHttpClient? = null): IHttpRequestController<T> {
        val request = Request.Builder()
            .headers(Headers.headersOf(*namesAndValues.toTypedArray()))
            .post(requestBody)
            .url(url)
            .build()
        return httpRequestFactory
            .createHttpRequest(
                callback ?: SimpleHttpCallback(),
                transformer ?: transformerFactory.createTransformer())
            .execute((specialClient ?: okHttpClient).newCall(request), resultType)
    }


    open class SimpleHttpCallback<T> : IHttpCallback<T> {
        private val tag = "HttpCallback"
        override fun onBefore() {
            log("onBefore - currentThread${Thread.currentThread()}")
        }

        override fun onBackground(response: Response): T? {
            log("onBackground - currentThread${Thread.currentThread()} \t response : $response")
            return null
        }

        override fun onSuccess(data: T) {
            log("onSuccess - currentThread${Thread.currentThread()} \tdata : $data")
        }

        override fun onFailure(e: Throwable) {
            log("onFailure - currentThread${Thread.currentThread()} \terrorMsg : ${e.message}")
        }

        override fun onCancel(e: Throwable?) {
            log("onCancel - currentThread${Thread.currentThread()} \terrorMsg : ${e?.message}")
        }

        override fun onFinish(data: T?, e: Throwable?) {
            log("onFinish - currentThread : ${Thread.currentThread()} \tdata : $data \terrorMsg : ${e?.message}")
        }

        protected fun log(message: String) {
            if (IS_DEBUG) {
                Log.d(tag, message)
            }
        }
    }

    open class DownloadCallback<T> : SimpleHttpCallback<T>(), IProgress {
        override fun onProgress(currentLength: Long, totalLength: Long, done: Boolean) {
            log("onProgress - currentThread : ${Thread.currentThread()} \tcurrentLength : $currentLength \ttotalLength : $totalLength")
        }
    }

    open class MediaFileUriCallback : DownloadCallback<Uri>()
    open class FileCallback : DownloadCallback<File?>()

    class Builder() {
        private var client: OkHttpClient? = null
        private var headers: ArrayList<String>? = null
        private var transformerFactory: ITransformerFactory? = null
        private var httpRequestFactory: IHttpRequestFactory? = null
        fun okHttpClient(client: OkHttpClient): Builder {
            this.client = client
            return this
        }

        /**
         * @param namesAndValues  name,value,name,value......
         */
        fun headers(vararg namesAndValues: String): Builder {
            if (headers == null) {
                headers = ArrayList()
            }
            headers?.clear()
            headers?.addAll(namesAndValues)
            return this
        }

        fun setTransformerFactory(transformerFactory: ITransformerFactory): Builder {
            this.transformerFactory = transformerFactory
            return this
        }

        fun setHttpRequestFactory(httpRequestFactory: IHttpRequestFactory): Builder {
            this.httpRequestFactory = httpRequestFactory
            return this
        }

        fun isDebug(boolean: Boolean): Builder {
            IS_DEBUG = boolean
            return this
        }

        fun build(): OkHttpRequest {
            client ?: throw NullPointerException("client is null.")
            transformerFactory ?: throw NullPointerException("jsonTransformerFactory is null")
            httpRequestFactory ?: throw NullPointerException("httpRequestFactory is null.")
            return OkHttpRequest(client!!, transformerFactory!!, httpRequestFactory!!).apply {
                setHeaders(headers)
            }
        }
    }
}