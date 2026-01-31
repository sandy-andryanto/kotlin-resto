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
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.frontend.app.R
import com.frontend.app.adapters.MenuAdapter
import com.frontend.app.adapters.OrderCurrentAdapter
import com.frontend.app.helpers.AppHelper
import com.frontend.app.pages.ChangePasswordActivity
import com.frontend.app.pages.CreateOrderActivity
import com.frontend.app.response.OrderResponse
import com.frontend.app.response.ProductResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
 * Use the [OrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _dataOriginals: List<OrderResponse> = emptyList()

    var dataOriginals: List<OrderResponse>
        get() = _dataOriginals
        set(value) {
            _dataOriginals = value
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

        val view = inflater.inflate(R.layout.fragment_order, container, false)
        val btnCreateOrder = view.findViewById<Button>(R.id.btnCreateOrder)
        btnCreateOrder.setOnClickListener {
            val intent = Intent(requireContext(), CreateOrderActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerOrderCurrent)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchEditText.addTextChangedListener { editable ->
            val keyword = editable.toString().lowercase()
            val result = dataOriginals.filter {
                it.order_number.lowercase().contains(keyword) ||
                        it.table_number.lowercase().contains(keyword) ||
                        it.order_type.lowercase().contains(keyword) ||
                        it.customer_name.lowercase().contains(keyword) ||
                        it.cashier_name.lowercase().contains(keyword)
            }
            recyclerView.adapter = OrderCurrentAdapter(result)
        }


        loadData(view)
    }

    private fun loadData(view: View) {
        try {

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerOrderCurrent)
            val shimmerLoader = view.findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
            val mainContent = view.findViewById<ScrollView>(R.id.mainContent)
            val client = AppHelper.getHttpClient()
            val request = AppHelper.getHttpRequest(requireContext(), "api/order/pending")

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
                            val type = object : TypeToken<List<OrderResponse>>() {}.type
                            val data: List<OrderResponse> = Gson().fromJson(responseBody, type)
                            shimmerLoader.stopShimmer()
                            shimmerLoader.visibility = View.GONE
                            mainContent.visibility = View.VISIBLE
                            recyclerView.adapter = OrderCurrentAdapter(data)
                            dataOriginals = data
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
         * @return A new instance of fragment OrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}