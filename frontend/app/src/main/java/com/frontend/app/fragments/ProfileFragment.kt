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

package com.frontend.app.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ScrollView
import com.facebook.shimmer.ShimmerFrameLayout
import com.frontend.app.R
import com.frontend.app.helpers.AppHelper
import com.frontend.app.pages.ChangePasswordActivity
import com.frontend.app.pages.DisconnectActivity
import com.frontend.app.pages.ForgotPasswordActivity
import com.frontend.app.pages.LoginActivity
import com.frontend.app.pages.MainAppActivity
import com.frontend.app.preferences.AppPreference
import com.frontend.app.requests.LoginRequest
import com.frontend.app.requests.ProfileRequest
import com.frontend.app.response.ErrorResponse
import com.frontend.app.response.LoginResponse
import com.frontend.app.response.SuccessResponse
import com.frontend.app.response.UserResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val genderOptions = listOf("Male", "Female")
    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val emailLayout = view.findViewById<TextInputLayout>(R.id.emailEditTextLayout)
        val phoneLayout = view.findViewById<TextInputLayout>(R.id.phoneEditTextLayout)
        val nameLayout = view.findViewById<TextInputLayout>(R.id.nameEditTextLayout)
        val addressLayout = view.findViewById<TextInputLayout>(R.id.addressEditTextLayout)
        val genderLayout = view.findViewById<TextInputLayout>(R.id.genderLayout)
        val txtEmail = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val txtPhone = view.findViewById<TextInputEditText>(R.id.phoneEditText)
        val txtName = view.findViewById<TextInputEditText>(R.id.nameEditText)
        val txtAddress = view.findViewById<TextInputEditText>(R.id.addressEditText)
        val genderDropdown = view.findViewById<AutoCompleteTextView>(R.id.genderDropdown)
        val adapter = ArrayAdapter( requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        genderDropdown.setAdapter(adapter)

        genderDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedGender = genderOptions[position].toString().lowercase()
        }

        val btnSubmit = view.findViewById<Button>(R.id.btnUpdate)
        btnSubmit.setOnClickListener {
            // submit form
            val email = txtEmail.text.toString().trim()
            val name = txtName.text.toString()
            val phone = txtPhone.text.toString()
            val address = txtAddress.text.toString()
            val gender = selectedGender.toString()
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

            if (name.isEmpty()) {
                nameLayout.error = "Name is required"
                isValid = false
            }

            if (phone.isEmpty()) {
                phoneLayout.error = "Phone number is required"
                isValid = false
            }

            if (gender.isEmpty()) {
                genderLayout.error = "Gender is required"
                isValid = false
            }

            if (address.isEmpty()) {
                addressLayout.error = "Address is required"
                isValid = false
            }

            if(isValid){
                val formData = ProfileRequest(email, name, gender, phone, address)
                submitForm(requireContext(), formData)
            }

        }

        val btnResetPassword = view.findViewById<Button>(R.id.btnResetPassword)
        btnResetPassword.setOnClickListener {
            // Go To Reset Password
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)
        btnLogOut.setOnClickListener {
            showConfirmationDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(view)
    }

    private fun submitForm(context: Context, postData: ProfileRequest){

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false) // optional
            .create()
        dialog.show()

        val client = AppHelper.getHttpClient()
        val request = AppHelper.postHttpRequest(context, "api/profile/update", postData)

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                (context as Activity).runOnUiThread{
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                        val responseBody = response.body?.string()
                        if(response.code == 200){
                            val resultResponse = Gson().fromJson(responseBody, SuccessResponse::class.java)
                            AppHelper.showToast(context, resultResponse.message, false)
                        }else{
                            val resultResponse = Gson().fromJson(responseBody, ErrorResponse::class.java)
                            AppHelper.showToast(context, resultResponse.error, true)
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

    private fun loadData(view: View){

        val mainContent = view.findViewById<ScrollView>(R.id.scrollView)
        val shimmerLoader = view.findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
        val client = AppHelper.getHttpClient()
        val request = AppHelper.getHttpRequest(requireContext(), "api/profile/detail")

        shimmerLoader.visibility = View.VISIBLE
        shimmerLoader.startShimmer()
        mainContent.visibility = View.GONE

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                (context as Activity).runOnUiThread{
                    println(e.toString())
                }

            }

            override fun onResponse(call: Call, response: Response) {
                (context as Activity).runOnUiThread{
                    Handler(Looper.getMainLooper()).postDelayed({
                        val responseBody = response.body?.string()
                        val user = Gson().fromJson(responseBody, UserResponse::class.java)
                        val txtEmail = view.findViewById<TextInputEditText>(R.id.emailEditText)
                        val txtPhone = view.findViewById<TextInputEditText>(R.id.phoneEditText)
                        val txtName = view.findViewById<TextInputEditText>(R.id.nameEditText)
                        val txtAddress = view.findViewById<TextInputEditText>(R.id.addressEditText)
                        val optGender = view.findViewById<AutoCompleteTextView>(R.id.genderDropdown)
                        txtEmail.setText(user.email.toString())
                        txtPhone.setText(user.phone.toString())
                        txtName.setText(user.name.toString())
                        txtAddress.setText(user.address.toString())

                        if(user.gender.toString().equals("male")){
                            optGender.setText("Male", false)
                        }else{
                            optGender.setText("Female", false)
                        }

                        shimmerLoader.stopShimmer()
                        shimmerLoader.visibility = View.GONE
                        mainContent.visibility = View.VISIBLE
                    }, 2000)
                }
            }

        })
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit Application")
            .setMessage("Do you want to confirm this action ?")
            .setPositiveButton("Yes, Continue") { dialog, _ ->
                val prefs = AppPreference(requireContext())
                val intent = Intent(requireContext(), LoginActivity::class.java)
                dialog.dismiss()
                prefs.clear()
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}