package com.aqrlei.litenet.transformer

import com.aqrlei.litenet.BuildConfig.logger
import com.aqrlei.litenet.IS_DEBUG
import com.aqrlei.litenet.ITransformer

/**
 * created by AqrLei on 2020/4/21
 */
abstract class AbstractTransformer<T> : ITransformer<T> {
    protected val tag = "Transformer"

    protected fun log(message:String){
        if (IS_DEBUG) {
            logger.d(tag, message)
        }
    }
}