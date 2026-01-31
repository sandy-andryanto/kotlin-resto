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
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.frontend.app.R
import com.frontend.app.helpers.AppHelper
import com.frontend.app.response.SummaryResponse
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frontend.app.adapters.ProductsAdapter
import android.view.Gravity
import androidx.core.content.ContextCompat
import android.widget.ImageView
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import com.bumptech.glide.Glide
import com.frontend.app.response.TableResponse

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var loading: Boolean = false

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData(view)
    }

    private fun loadData(view: View) {
        loading = true
        try {

            val container = view.findViewById<LinearLayout>(R.id.tableContainer)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerProduct)
            val txtRevenue = view.findViewById<TextView>(R.id.txtRevenue)
            val txtSales = view.findViewById<TextView>(R.id.txtSales)
            val txtDineIn = view.findViewById<TextView>(R.id.txtDineIn)
            val txtTakeAway = view.findViewById<TextView>(R.id.txtTakeAway)
            val shimmerLoader = view.findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
            val mainContent = view.findViewById<ScrollView>(R.id.mainContent)
            val client = AppHelper.getHttpClient()
            val request = AppHelper.getHttpRequest(requireContext(), "api/home/summary")

            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            shimmerLoader.visibility = View.VISIBLE
            shimmerLoader.startShimmer()
            mainContent.visibility = View.GONE

            client.newCall(request).enqueue(object : Callback {


                override fun onFailure(call: Call, e: IOException) {
                    (context as Activity).runOnUiThread{
                        loading = false
                        println(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    (context as Activity).runOnUiThread{
                        Handler(Looper.getMainLooper()).postDelayed({
                            loading = false
                            val responseBody = response.body?.string()
                            val data = Gson().fromJson(responseBody, SummaryResponse::class.java)

                            shimmerLoader.stopShimmer()
                            shimmerLoader.visibility = View.GONE
                            mainContent.visibility = View.VISIBLE

                            txtRevenue.setText("\$ ${String.format("%.2f", data.total_sales.toDouble())}")
                            txtSales.setText(data.total_orders.toString())
                            txtDineIn.setText(data.total_dine_in.toString())
                            txtTakeAway.setText(data.total_take_away.toString())

                            val items = data.tables
                            createTableGrid(container, items)

                            recyclerView.adapter = ProductsAdapter(data.products)

                        }, 2000)
                    }
                }

            })
        }catch (e: Exception){
            loading = false
            println("Something went wrong: ${e.message}")
        }
    }

    private fun createTableGrid(container: LinearLayout, items: List<TableResponse>) {
        container.removeAllViews()

        val context = container.context
        val itemsPerRow = 3

        for (i in items.indices step itemsPerRow) {
            val rowLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 3f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
            }

            for (j in i until (i + itemsPerRow).coerceAtMost(items.size)) {
                val item = items[j]

                val itemLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    background = ContextCompat.getDrawable(context, R.drawable.linear_border)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        rightMargin = 9
                    }
                    setPadding(4, 4, 4, 6)
                }

                val image = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(70.dp(context), 70.dp(context))
                }

                Glide.with(context)
                    .load("https://5an9y4lf0n50.github.io/demo-images/demo-resto/table.png")
                    .into(image)

                val labelName = TextView(context).apply {
                    text = item.name
                    setTextColor(Color.parseColor("#4B0082"))
                    textSize = 15f
                    setTypeface(typeface, Typeface.BOLD)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 2.dp(context)
                    }
                }

                val labelStatus = TextView(context).apply {
                    text = if (item.status.toInt() == 1) "Available" else "Reserved"
                    setTextColor(Color.parseColor( if (item.status.toInt() == 1) "#008000" else "#B22222"))
                    textSize = 15f
                    setTypeface(typeface, Typeface.BOLD)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 2.dp(context)
                        bottomMargin = 5.dp(context)
                    }
                }

                itemLayout.addView(image)
                itemLayout.addView(labelName)
                itemLayout.addView(labelStatus)

                rowLayout.addView(itemLayout)
            }

            container.addView(rowLayout)
        }
    }

    fun Int.dp(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}