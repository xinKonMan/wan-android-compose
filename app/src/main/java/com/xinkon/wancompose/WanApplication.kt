package com.xinkon.wancompose

import android.app.Application

/**
 * Created by cwl on 2024/2/26.
 */
class WanApplication: Application() {

    companion object {
        const val TAG = "WanApplication"
        private lateinit var instance: WanApplication

        @JvmStatic
        fun getInstance() = instance

        @JvmStatic
        fun getAppContext() = instance.applicationContext!!
    }
}