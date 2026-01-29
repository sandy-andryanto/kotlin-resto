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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.frontend.app.R
import com.frontend.app.adapters.CartAdapter
import com.frontend.app.helpers.AppHelper
import com.frontend.app.response.DetailOrderResponse
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class DetailOrderActivity : AppCompatActivity() {

    private var _status: Int = 1

    var status: Int
        get() = _status
        set(value) {
            _status = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_detail_order)

        val id = intent.getStringExtra("id").toString()
        val btnBackClicked = findViewById<Button>(R.id.btnBack)


        btnBackClicked.setOnClickListener {
            val intent = Intent(this, MainAppActivity::class.java)
            if(status == 1){
                intent.putExtra("tabActive", "history")
                startActivity(intent)
            }else{
                intent.putExtra("tabActive", "order")
                startActivity(intent)
            }
        }

        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rootView.viewTreeObserver.removeOnPreDrawListener(this)
                loadData(this@DetailOrderActivity, id)
                return true
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadData(context: Context, id:String) {
        try {

            val btnBackClicked = findViewById<Button>(R.id.btnBack)
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerProduct)
            val shimmerLoader = findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
            val mainContent = findViewById<LinearLayout>(R.id.mainContent)
            val client = AppHelper.getHttpClient()
            val request = AppHelper.getHttpRequest(context, "api/history/detail/${id}")
            val orderNumber = findViewById<TextView>(R.id.txtOrderId)
            val orderDate = findViewById<TextView>(R.id.txtOrderDate)
            val tableNumber = findViewById<TextView>(R.id.txtTableNumber)
            val orderType = findViewById<TextView>(R.id.txtOrderType)
            val customerName = findViewById<TextView>(R.id.txtCustomerName)
            val cashierName = findViewById<TextView>(R.id.txtCasheirName)
            val totalItem = findViewById<TextView>(R.id.txtTotalItem)
            val totalPaid = findViewById<TextView>(R.id.txtTotalPaid)

            recyclerView.layoutManager = LinearLayoutManager(context)

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
                            val data = Gson().fromJson(responseBody, DetailOrderResponse::class.java)
                            val order = data.order

                            orderNumber.text = order.order_number
                            orderDate.text = "${AppHelper.formatWithOrdinal(order.created_at)}"
                            tableNumber.text =  if (order.order_type == "Dine In") order.table_number else "-"
                            orderType.text = order.order_type
                            customerName.text = order.customer_name
                            cashierName.text = order.cashier_name
                            totalItem.text = "${order.total_item.toString()} pcs"
                            totalPaid.text = "\$ ${String.format("%.2f", order.total_paid.toDouble())}"
                            recyclerView.adapter = CartAdapter(data.cart)
                            status = order.status.toInt()

                            if(order.status.toInt() == 1){
                                btnBackClicked.text = "Back To History"
                            }else{
                                btnBackClicked.text = "Back To Current Order"
                            }

                            shimmerLoader.stopShimmer()
                            shimmerLoader.visibility = View.GONE
                            mainContent.visibility = View.VISIBLE
                        }, 2000)
                    }
                }

            })

        }catch (e: Exception) {
            println("Something went wrong : ${e.message}")
        }
    }

}