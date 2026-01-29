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
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.frontend.app.R
import com.frontend.app.adapters.OrderHistoryAdapter
import com.frontend.app.helpers.AppHelper
import com.frontend.app.response.OrderResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import android.text.Editable
import androidx.core.widget.NestedScrollView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _page: Int = 1
    private var _limit: Int = 10
    private var _search: String? = null

    var page: Int
        get() = _page
        set(value) {
            _page = value
        }

    var limit: Int
        get() = _limit
        set(value) {
            _limit = value
        }

    var search: String?
        get() = _search
        set(value) {
            _search = value
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
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val handler = Handler(Looper.getMainLooper())
        var workRunnable: Runnable? = null
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // cancel previous runnable
                workRunnable?.let { handler.removeCallbacks(it) }
            }

            override fun afterTextChanged(s: Editable?) {
                workRunnable = Runnable {
                    search = s.toString()
                    loadData(view)
                }
                handler.postDelayed(workRunnable!!, 500) // Delay in ms
            }
        })

        val scrollView = view.findViewById<NestedScrollView>(R.id.mainContent)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val content = scrollView.getChildAt(0) // ✅
            val diff = content.bottom - (scrollView.height + scrollY)

            if (diff <= 0) {
                page = page + 1
                loadData(view)
            }

            if (scrollY == 0) {
                if (page > 1) {
                    page = page - 1
                    loadData(view)
                }
            }
        }



        loadData(view)
    }

    private fun loadData(view: View) {
        try {

            val shimmerLoader = view.findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerOrder)
            val mainContent = view.findViewById<NestedScrollView>(R.id.mainContent)

            val client = AppHelper.getHttpClient()
            var url = "api/history/list?limit=${limit}&page=${page}"

            if(search !== null){
                url += "&search=${search}"
            }

            val request = AppHelper.getHttpRequest(requireContext(), url)

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
                            recyclerView.adapter = OrderHistoryAdapter(data)
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
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}