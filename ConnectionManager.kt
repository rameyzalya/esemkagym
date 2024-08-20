package com.example.myesemka.tools

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.lang.reflect.Method
import java.net.HttpURLConnection
import java.net.URL

class ConnectionManager {
    val BASE_URL = "http://10.0.2.2:8081/api"
    private suspend inline fun <reified T> request(
        url: String,
        method: String = "GET",
        requestBody: JSONObject? = null,
        authToken: String? = null
    ): T {
        return withContext(Dispatchers.IO) {
//            Log.d(TAG, "Request URL: $url")
            val conn = URL(url).openConnection() as HttpURLConnection

            //request method seusai dengan kebutuhan, jadi nilainya diisi dengan nilai method (GET/POST/PUT)
            conn.requestMethod = method

            //sesuai dengan formatnya butuh application/json
            conn.setRequestProperty("Content-Type", "application/json")

            //authToken berjalan hanya ketika nilainya tidak null jadi kalau null tidak jalan
            authToken?.let { conn.setRequestProperty("Authorization", "Bearer $it") }

            if (requestBody != null) {
                conn.doOutput = true
                OutputStreamWriter(conn.outputStream).use {
//                    Log.d(TAG, "Request Body: $requestBody")
                    it.write(requestBody.toString())
                }
            }

            val code = conn.responseCode
            val body = conn.inputStream?.bufferedReader()?.use(BufferedReader::readText)
//            Log.d(TAG, "Response Code: $code")
//            Log.d(TAG, "Response Body: $body")

            if (code in 200 until 300) {
                when (T::class) {
                    //
                    String::class -> body as T
                    JSONObject::class -> JSONObject(body) as T
                    JSONArray::class -> JSONArray(body) as T
                    else -> throw IllegalStateException("Unsupported type")
                }
            } else {
                val errorBody = conn.errorStream?.bufferedReader()?.use(BufferedReader::readText)
                val errorMessage = JSONObject(errorBody ?: "").optString("message", "Request failed with code: $code")
//                Log.e(TAG, "Error Body: $errorBody")
                throw handleHttpException(code, errorMessage)
            }
        }
    }

    private fun handleHttpException(code: Int, message: String): Exception {
        return when (code) {
            401 -> NetworkException.AuthException()
            403 -> NetworkException.AuthException()
            else -> NetworkException.IgnorableException(message)
        }
    }

    suspend fun getCode(token : String?) : String {
        val url = "$BASE_URL/attendance/checkin/code"

        return request(url,"GET", null, token)
    }

    suspend fun approveMembership(memberId : String, token: String?) : JSONObject {
        val url = "$BASE_URL/member/$memberId/approve"
        return request(url, "PUT", null, token)
    }

    suspend fun resumeMembership(memberId: String, token: String?) : JSONObject {
        val url = "$BASE_URL/member/$memberId/resume"
        return request(url, "PUT", null, token)
    }

    suspend fun getMember(name : String?, status : String, token: String?) : JSONArray {
        val url = "$BASE_URL/member?status=$status${name?.let { "&name=$it" } ?: ""}"
        return request(url, "GET", null, token)
    }

    suspend fun login(email: String, password: String): JSONObject {
        val requestBody = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        return request("$BASE_URL/login", "POST", requestBody)
    }
    suspend fun signup(email: String, password: String, nama: String, gender: String): JSONObject {
        val requestBody = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("nama", nama)
            put("gender", gender)
        }
        return request("$BASE_URL/signup", "POST", requestBody)
    }
    suspend fun getAttendance(token: String?) :JSONArray {
        val url = "$BASE_URL/attendance"
        return request(url, "GET", null, token)
    }

    suspend fun checkInMember(code: String, token: String?) : JSONObject {
        val url = "$BASE_URL/attendance/checkin/$code"
        return request(url, "POST", null, token)
    }

    suspend fun checkoutMember(token: String?) : JSONObject {
        val url = "$BASE_URL/attendance/checkout"
        return request(url, "POST", null, token)
    }
}