package com.aqrlei.litenet.sample

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.aqrlei.utilcollection.toast.BaseToast
import kotlinx.android.synthetic.main.toast.view.*

/**
 * created by AqrLei on 2020/3/17
 */
class ToastWrapper(private val context: Context) : BaseToast() {

    override fun initToast(type: Int?): Toast {
        return NormalToast(context).instance
    }

    override fun getTextViewID(type: Int?): Int = R.id.tvToast

    internal class NormalToast(context: Context) {
        val instance = context.let { context ->
            val view = View.inflate(context, R.layout.toast, null)
            val w = context.resources.displayMetrics.widthPixels
            val h = context.resources.displayMetrics.heightPixels
            view.tvToast.minWidth = w / 4
            view.tvToast.maxWidth = (w * 3) / 4

            Toast(context).also {
                it.view = view
                it.setGravity(Gravity.BOTTOM, 0, h / 8)
            }
        }
    }
}