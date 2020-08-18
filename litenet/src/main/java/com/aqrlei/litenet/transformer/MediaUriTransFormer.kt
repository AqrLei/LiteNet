package com.aqrlei.litenet.transformer

import android.content.ContentResolver
import android.net.Uri
import com.aqrlei.litenet.IProgress
import okhttp3.ResponseBody
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/4/9
 */
class MediaUriTransFormer(
    private val uri: Uri,
    private val contentResolver: ContentResolver,
    private val processCallback: IProgress) :
    AbstractTransformer<Uri>() {

    override fun transformer(response: ResponseBody, resultType: Type): Uri{
        val inputStream = response.byteStream()
        inputStream.use {
          alertDocument(contentResolver, uri) { fos ->
                val buffer = ByteArray(2048)
                var len: Int
                val totalLength = response.contentLength()
                var currentLength = 0L
                while (inputStream.read(buffer).apply { len = this } > 0) {
                    fos.write(buffer, 0, len)
                    fos.flush()
                    currentLength += len.toLong()
                    log("currentLength : ${currentLength}, totalLength : $totalLength")
                    if (totalLength > 0) {
                        processCallback.onProgress(
                            currentLength,
                            totalLength,
                            currentLength == totalLength)
                    }
                }
            }
        }
        return uri
    }

    private fun alertDocument(
        contentResolver: ContentResolver,
        uri: Uri,
        callback: (fileOutputStream: FileOutputStream) -> Unit) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { fos ->
                    callback(fos)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}