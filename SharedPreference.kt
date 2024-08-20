package com.example.myesemka.tools

import android.content.Context
import org.json.JSONObject

class SharedPreference {
    companion object {
        fun Context.getAuth(): JSONObject? {
            return getSharedPreferences("TOKEN", Context.MODE_PRIVATE).getString("auth", null)?.let {
                return@let JSONObject(it)
            }
        }
        fun Context.setAuth(json: JSONObject) {
            getSharedPreferences("TOKEN", Context.MODE_PRIVATE).edit().putString("auth", json.toString()).apply()
        }
        fun Context.clearAuth() {
            getSharedPreferences("TOKEN", Context.MODE_PRIVATE).edit().remove("auth").apply()
        }
    }
}