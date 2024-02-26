package com.xinkon.wancompose.utils

import android.util.Log
import com.xinkon.wancompose.BuildConfig

fun showLog(showLog: () -> Unit) {
    if (true) showLog()
}

fun loge(tag: String, msg: String?) = showLog { Log.e(tag, msg, null) }
fun loge(tag: String, e: Throwable?, msg: String? = "") = showLog { Log.e(tag, msg, e) }
fun logd(tag: String, msg: String) = showLog { Log.d(tag, msg) }
fun logw(tag: String, msg: String) = showLog { Log.w(tag, msg) }
fun logv(tag: String, msg: String) = showLog { Log.v(tag, msg) }
fun logi(tag: String, msg: String) = showLog { Log.i(tag, msg) }
fun printThread(tag: String, msg: String) = loge(tag, "$msg in Thread:${Thread.currentThread().toString()}")

fun List<Any>.listLog(tag: String, msg: String) {
    logi(tag, msg)
    this.forEachIndexed { index, any ->
        logi(tag, "index $index : $any")
    }
}