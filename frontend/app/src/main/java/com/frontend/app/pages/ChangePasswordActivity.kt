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

package com.frontend.app.pages

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.frontend.app.R
import com.frontend.app.helpers.AppHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.frontend.app.requests.PasswordRequest
import com.frontend.app.response.ErrorResponse
import com.frontend.app.response.SuccessResponse
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_change_password)

        val passwordCurrentLayout = findViewById<TextInputLayout>(R.id.currentPasswordEditTextLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordEditTextLayout)
        val passwordConfirmLayout = findViewById<TextInputLayout>(R.id.passwordConfirmEditTextLayout)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordEditText)
        val passwordConfirmInput = findViewById<TextInputEditText>(R.id.passwordConfirmEditText)
        val passwordCurrentInput = findViewById<TextInputEditText>(R.id.currentPasswordEditText)

        val btnBackClicked = findViewById<Button>(R.id.btnBack)
        btnBackClicked.setOnClickListener {
            val intent = Intent(this, MainAppActivity::class.java)
            intent.putExtra("tabActive", "profile")
            startActivity(intent)
        }

        val btnSubmit = findViewById<Button>(R.id.btnChangePassword)
        btnSubmit.setOnClickListener {

            val currentPassword = passwordCurrentInput.text.toString()
            val password = passwordInput.text.toString()
            val passwordConfirm = passwordConfirmInput.text.toString()
            var isValid = true

            if (currentPassword.isEmpty()) {
                passwordCurrentLayout.error = "Current Password is required"
                isValid = false
            }

            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error = "Password must be at least 6 characters"
                isValid = false
            } else {
                passwordLayout.error = null
            }

            if (passwordConfirm.isEmpty()) {
                passwordConfirmLayout.error = "Confirm Password is required"
                isValid = false
            } else if (passwordConfirm != password) {
                passwordConfirmLayout.error = "Password confirm do not match"
                isValid = false
            } else {
                passwordConfirmLayout.error = null
            }

            if(isValid){
                val formData = PasswordRequest(currentPassword, password, passwordConfirm)
                submit(this, formData)
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun submit(context: Context, postData: PasswordRequest){

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // optional
            .create()
        dialog.show()

        val passwordCurrentLayout = findViewById<TextInputLayout>(R.id.currentPasswordEditTextLayout)
        passwordCurrentLayout.error = ""

        val client = AppHelper.getHttpClient()
        val request = AppHelper.postHttpRequest(context, "api/profile/password", postData)

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                (context as Activity).runOnUiThread{
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                        val responseBody = response.body?.string()
                        if(response.code == 200){
                            val successResponse = Gson().fromJson(responseBody, SuccessResponse::class.java)
                            AppHelper.showToast(context, successResponse.message, false)
                        }else{
                            val errorResponse = Gson().fromJson(responseBody, ErrorResponse::class.java)
                            passwordCurrentLayout.error = errorResponse.error
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