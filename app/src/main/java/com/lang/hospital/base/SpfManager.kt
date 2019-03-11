package com.js.common.data

import android.content.Context
import android.content.SharedPreferences
import com.lang.hospital.utils.MyLog

/**
 * Created by JS_WL on 2018/3/8 14:11.
 */

object SpfManager {
    private lateinit var spf: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    fun initData(context: Context) {
        spf = context.getSharedPreferences("sh_spf", Context.MODE_PRIVATE)
        MyLog.debug("$spf")
        edit = spf.edit()
    }

    fun putData(key: String, value: String) {
        edit.putString(key, value).commit()
    }

    fun getStringData(key: String): String {
        return spf.getString(key, "")
    }

    fun putData(key: String, value: Boolean) {
        edit.putBoolean(key, value).commit()
    }

    fun getBoolean(key: String): Boolean {
        return spf.getBoolean(key, false)
    }

    fun putData(key: String, value: Int) {
        edit.putInt(key, value).commit()
    }

    fun getInt(key: String): Int {
        return spf.getInt(key, -1)
    }
}
