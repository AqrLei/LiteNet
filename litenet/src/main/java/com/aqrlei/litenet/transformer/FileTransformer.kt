package com.aqrlei.litenet.transformer

import com.aqrlei.litenet.IProgress
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type

/**
 * created by AqrLei on 2020/3/23
 */
class FileTransformer(
    private val fileName: String,
    private val fileSuffix: String,
    private val directory: File,
    private val progressCallback: IProgress) :
    AbstractTransformer<File?>() {
    override fun transformer(response: ResponseBody, resultType: Type): File? {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File.createTempFile(fileName, fileSuffix, directory)
        if (!file.canWrite()) throw IOException("${file.absolutePath} cannot write.")
        val inputStream = response.byteStream()
        val fos = FileOutputStream(file)
        try {
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
                    progressCallback.onProgress(
                        currentLength,
                        totalLength,
                        currentLength == totalLength)
                }
            }
        } finally {
            try {
                inputStream.close()
            } catch (e: Exception) {
                log(e.message ?: "empty")
            }
            try {
                fos.close()
            } catch (e: Exception) {
                log(e.message ?: "empty")
            }
        }
        return file
    }
}