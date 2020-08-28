package com.aqrlei.litenet.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aqrlei.litenet.OkHttpHelper
import com.aqrlei.litenet.OkHttpRequest
import com.aqrlei.litenet.request.coroutine.CoroutineHttpRequestFactory
import com.aqrlei.litenet.transformer.common.FileTransformer
import com.aqrlei.litenet.transformer.moshi.MoshiJsonTransformerFactory
import com.aqrlei.utilcollection.CacheFileUtil
import com.aqrlei.utilcollection.toast.ToastHelper
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

class MainActivity : AppCompatActivity() {

    private val downloadUrl = "https://download.mockuai.com/app/mkseller.apk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ToastHelper.initToast(ToastWrapper(this))
        initLiteNet()
        setListener()
    }

    private fun initLiteNet() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        OkHttpHelper.init(
            OkHttpRequest.Builder()
                .okHttpClient(okHttpClient)
                .setTransformerFactory(MoshiJsonTransformerFactory.create())
                .setHttpRequestFactory(CoroutineHttpRequestFactory.create())
                .isDebug(true)
                .build()
        )
    }

    private fun setListener() {
        btn.setOnClickListener {
            download()
        }
    }

    private fun download() {
        val callback = object : OkHttpRequest.FileCallback() {
            override fun onBefore() {
                super.onBefore()
                Log.d("AqrLei","onBefore-thread=${Thread.currentThread()}")
                ToastHelper.longShow("开始下载")
            }

            override fun onBackground(response: Response): File? {
                Log.d("AqrLei","onBackground-thread=${Thread.currentThread()}")
                return super.onBackground(response)
            }

            override fun onCancel(e: Throwable?) {
                super.onCancel(e)
                Log.d("AqrLei","onCancel-thread=${Thread.currentThread()}")
            }

            override fun onProgress(currentLength: Long, totalLength: Long, done: Boolean) {
                super.onProgress(currentLength, totalLength, done)
//                Log.d("AqrLei","onProgress-thread=${Thread.currentThread()}")
                val progress = (currentLength / totalLength.toDouble() * 100).toInt()
//                Log.d("AqrLei", "progress = $progress")


                progressBar.progress = progress
            }

            override fun onSuccess(data: File?) {
                super.onSuccess(data)
                Log.d("AqrLei","onSuccess-thread=${Thread.currentThread()}")
                data?.let {
                    Log.d("AqrLei","file-path=${it.absolutePath}")
                }
                ToastHelper.longShow("下载完毕")
            }

            override fun onFailure(e: Throwable) {
                super.onFailure(e)
                Log.d("AqrLei","onFailure-thread=${Thread.currentThread()}")
            }

            override fun onFinish(data: File?, e: Throwable?) {
                super.onFinish(data, e)
                Log.d("AqrLei","onFinish-thread=${Thread.currentThread()}")
            }
        }
        OkHttpHelper.download(
            downloadUrl,
            File::class.java,
            callback,
            FileTransformer(
                "qexdTest",
                ".apk",
                CacheFileUtil.getAppFilesDirFile(this, "apk"),
                callback
            )
        )
    }
}