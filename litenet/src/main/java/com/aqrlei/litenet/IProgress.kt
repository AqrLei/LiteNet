package com.aqrlei.litenet

/**
 * created by AqrLei on 2020/3/23
 */
interface IProgress {
    fun onProgress(currentLength: Long, totalLength: Long, done:Boolean)
}