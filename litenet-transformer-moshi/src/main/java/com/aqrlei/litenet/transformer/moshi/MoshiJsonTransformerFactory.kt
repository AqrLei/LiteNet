package com.aqrlei.litenet.transformer.moshi

import com.aqrlei.litenet.ITransformer
import com.aqrlei.litenet.ITransformerFactory

/**
 * created by AqrLei on 2020/3/20
 */
class MoshiJsonTransformerFactory private constructor() :
    ITransformerFactory {
    companion object {
        fun create() =
            MoshiJsonTransformerFactory()
    }

    override fun <T> createTransformer(): ITransformer<T> {
        return MoshiJsonTransformer.createJsonParser()
    }
}