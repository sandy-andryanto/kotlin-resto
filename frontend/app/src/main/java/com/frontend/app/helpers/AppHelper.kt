/**
 * This file is part of the Sandy Andryanto Sandy Resto Application.
 *
 * @author Sandy Andryanto <sandy.andryanto.blade@gmail.com>
 * @copyright 2025
 *
 * For the full copyright and license information,
 * please view the LICENSE.md file that was distributed
 * with this source code.
 */

package com.frontend.app.helpers

import android.content.Context
import android.widget.Toast
import android.view.Gravity
import android.graphics.Color
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import com.frontend.app.R
import com.frontend.app.preferences.AppPreference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Properties
import com.google.gson.Gson
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

class AppHelper {

    companion object {

        @JvmStatic
        fun showToast(context: Context, message: String, isError: Boolean) {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.toast_app, null)

            val textView = layout.findViewById<TextView>(R.id.toastText)
            val iconView = layout.findViewById<ImageView>(R.id.toastIcon)

            textView.text = message

            if (isError) {
                layout.setBackgroundColor(Color.parseColor("#F44336"))
                iconView.setImageResource(R.drawable.ic_error)
            } else {
                layout.setBackgroundColor(Color.parseColor("#4CAF50"))
                iconView.setImageResource(R.drawable.ic_success)
            }

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.setMargin(0.1f, 0.0f)
            toast.show()
        }

        @JvmStatic
        fun loadEnv(context: Context): Properties {
            val props = Properties()
            context.assets.open("env.properties").use { inputStream ->
                props.load(inputStream)
            }
            return props
        }

        @JvmStatic
        fun getHttpClient() : OkHttpClient{

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        @JvmStatic
        fun getHttpRequest(context: Context, path:String) : Request{
            val props = loadEnv(context)
            val baseUrl = props.getProperty("APP_BACKEND_URL")
            val pref = AppPreference(context)
            if(pref.isLoggedIn()){
                val token = pref.getToken().toString()
                return Request.Builder()
                    .url("$baseUrl/$path")
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }else{
                return Request.Builder()
                    .url("$baseUrl/$path")
                    .build()
            }
        }

        @JvmStatic
        fun formatWithOrdinal(date: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val suffix = getDaySuffix(day)

            val dateFormat = SimpleDateFormat("MMMM d'$suffix' yyyy, h:mm:ss a", Locale.ENGLISH)
            return dateFormat.format(date)
        }

        @JvmStatic
        fun getDaySuffix(day: Int): String {
            return when {
                day in 11..13 -> "th"
                day % 10 == 1 -> "st"
                day % 10 == 2 -> "nd"
                day % 10 == 3 -> "rd"
                else -> "th"
            }
        }

        @JvmStatic
        fun postHttpRequest(context: Context, path:String, body:Any) : Request{

            val gson = Gson()
            val json = gson.toJson(body)
            val props = loadEnv(context)
            val baseUrl = props.getProperty("APP_BACKEND_URL")
            val pref = AppPreference(context)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            if(pref.isLoggedIn()){
                val token = pref.getToken().toString()
                return Request.Builder()
                    .url("$baseUrl/$path")
                    .addHeader("Authorization", "Bearer $token")
                    .post(requestBody)
                    .build()
            }else{
                return Request.Builder()
                    .url("$baseUrl/$path")
                    .post(requestBody)
                    .build()
            }

        }

    }

}