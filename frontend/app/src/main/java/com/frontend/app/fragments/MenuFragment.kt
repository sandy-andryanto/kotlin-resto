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

package com.frontend.app.fragments

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.frontend.app.R
import com.frontend.app.adapters.MenuAdapter
import com.frontend.app.helpers.AppHelper
import com.frontend.app.response.ProductResponse
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import androidx.core.widget.addTextChangedListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _menuOriginals: List<ProductResponse> = emptyList()
    private var _filtered: String = "all"

    var menuOriginals: List<ProductResponse>
        get() = _menuOriginals
        set(value) {
            _menuOriginals = value
        }

    var filtered: String
        get() = _filtered
        set(value) {
            _filtered = value
        }

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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerProduct)
        val chipAppetizer = view.findViewById<Chip>(R.id.chipAppetizer)
        val chipMainCourse = view.findViewById<Chip>(R.id.chipMainCourse)
        val chipDessert = view.findViewById<Chip>(R.id.chipDessert)
        val chipAll = view.findViewById<Chip>(R.id.chipAll)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val selectedColor = "#FFA500"

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        chipAppetizer.setOnClickListener {
            filtered = "Appetizer"
            chipAppetizer.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(selectedColor))
            chipMainCourse.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C71585"))
            chipDessert.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#808000"))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#2F4F4F"))
            val filtered = menuOriginals.filter { it.category == "Appetizer" }
            recyclerView.adapter = MenuAdapter(filtered)
        }

        chipMainCourse.setOnClickListener {
            filtered = "Main Course"
            chipAppetizer.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#0000CD"))
            chipMainCourse.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(selectedColor))
            chipDessert.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#808000"))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#2F4F4F"))
            val filtered = menuOriginals.filter { it.category == "Main Course" }
            recyclerView.adapter = MenuAdapter(filtered)
        }

        chipDessert.setOnClickListener {
            filtered = "Dessert"
            chipAppetizer.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#0000CD"))
            chipMainCourse.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C71585"))
            chipDessert.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(selectedColor))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#2F4F4F"))
            val filtered = menuOriginals.filter { it.category == "Dessert" }
            recyclerView.adapter = MenuAdapter(filtered)
        }

        chipAll.setOnClickListener {
            filtered = "All"
            chipAppetizer.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#0000CD"))
            chipMainCourse.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C71585"))
            chipDessert.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#808000"))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(selectedColor))
            recyclerView.adapter = MenuAdapter(menuOriginals)
        }

        searchEditText.addTextChangedListener { editable ->
            val keyword = editable.toString().lowercase()
            val result = menuOriginals.filter {
                it.name.lowercase().contains(keyword) ||
                        it.category.lowercase().contains(keyword)
            }
            recyclerView.adapter = MenuAdapter(result)
        }

        loadData(view)
    }

    private fun loadData(view: View) {
        try {

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerProduct)
            val shimmerLoader = view.findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
            val mainContent = view.findViewById<ScrollView>(R.id.mainContent)
            val client = AppHelper.getHttpClient()
            val request = AppHelper.getHttpRequest(requireContext(), "api/menu/list")

            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            shimmerLoader.visibility = View.VISIBLE
            shimmerLoader.startShimmer()
            mainContent.visibility = View.GONE

            client.newCall(request).enqueue(object : Callback {


                override fun onFailure(call: Call, e: IOException) {
                    (context as Activity).runOnUiThread {
                        println(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    (context as Activity).runOnUiThread {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val responseBody = response.body?.string()
                            val type = object : TypeToken<List<ProductResponse>>() {}.type
                            val data: List<ProductResponse> = Gson().fromJson(responseBody, type)
                            shimmerLoader.stopShimmer()
                            shimmerLoader.visibility = View.GONE
                            mainContent.visibility = View.VISIBLE
                            recyclerView.adapter = MenuAdapter(data)
                            menuOriginals = data
                        }, 2000)
                    }
                }

            })

        } catch (e: Exception) {
            println("Something went wrong: ${e.message}")
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}