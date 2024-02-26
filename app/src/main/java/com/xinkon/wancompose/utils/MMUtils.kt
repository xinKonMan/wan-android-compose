package com.xinkon.wancompose.utils

import android.content.Context
import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 * Created by linmaoxin on 2021/3/25
 * @see <a href="https://github.com/Tencent/MMKV/blob/master/README_CN.md">使用腾讯MMKV方案替代SharedPreferences</a>
 */
object MMUtils {
    /**
     * @param context application context
     */
    @JvmStatic
    fun initialize(context: Context) {
        MMKV.initialize(context.applicationContext)
    }

    /**
     * @param name 业务存储名
     * @param mode 进程模式
     */
    @JvmStatic
    @JvmOverloads
    fun with(name: String, mode: Int = MMKV.MULTI_PROCESS_MODE): MM {
        return MM().with(name, mode)
    }

    class MM {
        private lateinit var mmkv: MMKV

        @JvmOverloads
        fun with(name: String, mode: Int = MMKV.MULTI_PROCESS_MODE): MM {
            mmkv = MMKV.mmkvWithID(name, mode)!!
            return this
        }

        /** 设置k-v
         * @param key key
         * @param value 支持 String、Boolean、Float、Int、Long、Double、ByteArray、Parcelable
         */
        fun put(key: String, value: Any?): MM {
            when (value) {
                is String -> mmkv.encode(key, value)
                is Boolean -> mmkv.encode(key, value)
                is Float -> mmkv.encode(key, value)
                is Int -> mmkv.encode(key, value)
                is Long -> mmkv.encode(key, value)
                is Double -> mmkv.encode(key, value)
                is ByteArray -> mmkv.encode(key, value)
                is Parcelable -> mmkv.encode(key, value)
            }
            return this
        }

        fun remove(key: String): MM {
            mmkv.remove(key)
            return this
        }

        fun clearAll() {
            mmkv.clearAll()
        }

        fun getMMKV(): MMKV {
            return mmkv
        }

        @JvmOverloads
        fun getString(key: String, def: String? = String.EMPTY): String? =
            mmkv.decodeString(key, def)

        @JvmOverloads
        fun getBoolean(key: String, def: Boolean = false): Boolean = mmkv.decodeBool(key, def)

        @JvmOverloads
        fun getFloat(key: String, def: Float = 0f) = mmkv.decodeFloat(key, def)

        @JvmOverloads
        fun getDouble(key: String, def: Double = 0.0) = mmkv.decodeDouble(key, def)

        @JvmOverloads
        fun getInt(key: String, def: Int = 0) = mmkv.decodeInt(key, def)

        @JvmOverloads
        fun getLong(key: String, def: Long = 0) = mmkv.decodeLong(key, def)

        @JvmOverloads
        fun <T : Parcelable> getParcelable(key: String, tClass: Class<T>) =
            mmkv.decodeParcelable(key, tClass)

        @JvmOverloads
        fun getBytes(key: String, def: ByteArray? = null):ByteArray? = mmkv.decodeBytes(key, def)
    }
}