package com.aqrlei.litenet.transformer.json.factory

import com.aqrlei.litenet.ITransformer
import com.aqrlei.litenet.ITransformerFactory
import com.aqrlei.litenet.transformer.json.MoshiJsonTransformer

/**
 * created by AqrLei on 2020/3/20
 */
class DefaultJsonTransformerFactory private constructor() :
    ITransformerFactory {
    companion object {
        fun create() =
            DefaultJsonTransformerFactory()
    }

    override fun <T> createTransformer(): ITransformer<T> {
        return MoshiJsonTransformer.createJsonParser()
    }
}