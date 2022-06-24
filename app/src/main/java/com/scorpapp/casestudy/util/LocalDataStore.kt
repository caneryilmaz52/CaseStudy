package com.scorpapp.casestudy.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class LocalDataStore(activity: Activity) {

    private var sharedPreferences: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveData(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getData(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }
}