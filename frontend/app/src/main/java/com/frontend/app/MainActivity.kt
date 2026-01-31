/**
 * This file is part of the Sandy Andryanto Sandy Resto Application.
 *
 * @author Sandy Andryanto <sandy.andryanto.official@gmail.com>
 * @copyright 2025
 *
 * For the full copyright and license information,
 * please view the LICENSE.md file that was distributed
 * with this source code.
 */

package com.frontend.app

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import android.os.Handler
import android.os.Looper
import android.content.Intent
import android.view.View
import android.view.ViewTreeObserver
import com.frontend.app.pages.LoginActivity
import com.frontend.app.helpers.AppHelper
import com.frontend.app.pages.CreateOrderActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import com.frontend.app.pages.DisconnectActivity
import com.frontend.app.pages.MainAppActivity
import com.frontend.app.preferences.AppPreference

class MainActivity : AppCompatActivity() {

    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val rootView = findViewById<View>(android.R.id.content)


        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rootView.viewTreeObserver.removeOnPreDrawListener(this)
                ping(this@MainActivity)
                return true
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun loadCustomAnimation(fileName: String) {
        val assetPath = "lottie/$fileName.json"
        lottieAnimationView.setAnimation(assetPath)
        lottieAnimationView.playAnimation()
    }

    fun ping(context: Context) {

        val client = AppHelper.getHttpClient()
        val request = AppHelper.getHttpRequest(context, "api/ping")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@MainActivity, DisconnectActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2500)
            }

            override fun onResponse(call: Call, response: Response) {
                val prefs = AppPreference(context)
                val logged = prefs.isLoggedIn()
                if(logged){
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@MainActivity, MainAppActivity::class.java)
                        intent.putExtra("tabActive", "home")
                        startActivity(intent)
                        finish()
                    }, 2500)
                }else{
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2500)
                }
            }
        })
    }


}