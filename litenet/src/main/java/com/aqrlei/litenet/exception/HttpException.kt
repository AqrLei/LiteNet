package com.aqrlei.litenet.exception

import okhttp3.Response

/**
 * created by AqrLei on 2020/3/19
 */
class HttpException(@Transient val response: Response) : RuntimeException(
    getMessage(
        response)) {
    companion object {
        fun getMessage(response: Response): String {
            return "HTTP ${response.code} ${response.message}"
        }
    }

    fun code() = response.code

    fun message() = response.message
}