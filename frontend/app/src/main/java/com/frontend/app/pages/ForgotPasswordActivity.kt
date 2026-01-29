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

package com.frontend.app.pages

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.frontend.app.R
import com.frontend.app.helpers.AppHelper
import com.frontend.app.requests.ForgotPasswordRequest
import com.frontend.app.response.ErrorResponse
import com.frontend.app.response.ForgotPasswordResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_forgot_password)

        val imageView = findViewById<ImageView>(R.id.imageFromUrl)
        Glide.with(this)
            .load("https://5an9y4lf0n50.github.io/demo-images/demo-resto/burger.png")
            .into(imageView)

        val emailLayout = findViewById<TextInputLayout>(R.id.emailEditTextLayout)
        val emailInput = findViewById<TextInputEditText>(R.id.emailEditText)

        val btnLoginClicked = findViewById<Button>(R.id.btnLogin)
        btnLoginClicked.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val btnForgotClicked = findViewById<Button>(R.id.btnForgotPassword)
        btnForgotClicked.setOnClickListener {

            val email = emailInput.text.toString().trim()
            var isValid = true

            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Invalid email format"
                isValid = false
            } else {
                emailLayout.error = null
            }

            if (isValid) {
                doSubmit(this@ForgotPasswordActivity, email)
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun doSubmit(context: Context, email:String) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // optional
            .create()
        dialog.show()

        val postData = ForgotPasswordRequest(email)
        val client = AppHelper.getHttpClient()
        val request = AppHelper.postHttpRequest(context, "api/auth/email/forgot", postData)

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                (context as Activity).runOnUiThread{
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                        val responseBody = response.body?.string()
                        if(response.code == 200){
                            val forgotPasswordResponse = Gson().fromJson(responseBody, ForgotPasswordResponse::class.java)
                            val intent = Intent(context, ResetPasswordActivity::class.java)
                            intent.putExtra("token", forgotPasswordResponse.token)
                            AppHelper.showToast(context, forgotPasswordResponse.message, false)
                            startActivity(intent)
                        }else{
                            val errorResponse = Gson().fromJson(responseBody, ErrorResponse::class.java)
                            val emailLayout = findViewById<TextInputLayout>(R.id.emailEditTextLayout)
                            emailLayout.error = errorResponse.error
                        }
                    }, 2000)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                (context as Activity).runOnUiThread{
                    dialog.dismiss()
                    println(e)
                }
            }

        })

    }

}