package com.frontend.app.pages

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.frontend.app.R
import com.frontend.app.adapters.MenuOrderAdapter
import com.frontend.app.adapters.MenuOrderHandleAdapter
import com.frontend.app.helpers.AppHelper
import com.frontend.app.requests.OrderRequest
import com.frontend.app.response.CartResponse
import com.frontend.app.response.ErrorResponse
import com.frontend.app.response.OrderInitResponse
import com.frontend.app.response.ProductResponse
import com.frontend.app.response.SuccessResponse
import com.frontend.app.response.TableResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class UpdateOrderActivity : AppCompatActivity() {

    private lateinit var viewMenu: View
    private lateinit var viewOrder: View
    private lateinit var viewCheckOut: View
    private var _tables: List<TableResponse> = mutableListOf()
    private var _cart: MutableList<CartResponse> = mutableListOf()
    private var _menuOriginals: List<ProductResponse> = emptyList()
    private var _filtered: String = "all"
    private var _orderNumber: String = ""
    private var selectedTableNumber: String? = null

    var tables: List<TableResponse>
        get() = _tables
        set(value) {
            _tables = value
        }

    var cart: MutableList<CartResponse>
        get() = _cart
        set(value) {
            _cart = value
        }

    var orderNumber: String
        get() = _orderNumber
        set(value) {
            _orderNumber = value
        }

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
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_update_order)

        val id = intent.getStringExtra("id").toString()
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val recyclerProduct = findViewById<RecyclerView>(R.id.recyclerProduct)
        val recyclerOrderDetail = findViewById<RecyclerView>(R.id.recyclerOrderDetail)
        val btnBackClicked = findViewById<Button>(R.id.btnCloseOrder)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavOrder)
        val chipAppetizer = findViewById<Chip>(R.id.chipAppetizer)
        val chipMainCourse = findViewById<Chip>(R.id.chipMainCourse)
        val chipDessert = findViewById<Chip>(R.id.chipDessert)
        val chipAll = findViewById<Chip>(R.id.chipAll)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val selectedColor = "#FFA500"
        val txtCustomerNameLayout = findViewById<TextInputLayout>(R.id.txtCustomerNameLayout)
        val txtCustomerName = findViewById<TextInputEditText>(R.id.txtCustomerName)
        val optionTableNumberLayout = findViewById<TextInputLayout>(R.id.optionTableNumberLayout)
        val optionTableNumber = findViewById<AutoCompleteTextView>(R.id.optionTableNumber)

        viewMenu = findViewById(R.id.viewMenu)
        viewOrder = findViewById(R.id.viewOrder)
        viewCheckOut = findViewById(R.id.viewCheckOut)

        recyclerProduct.layoutManager = LinearLayoutManager(this@UpdateOrderActivity)
        recyclerOrderDetail.layoutManager = LinearLayoutManager(this@UpdateOrderActivity)

        btnBackClicked.setOnClickListener {
            val intent = Intent(this, MainAppActivity::class.java)
            intent.putExtra("tabActive", "order")
            startActivity(intent)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_order_menu -> showOnly(viewMenu)
                R.id.nav_order_detail -> showOnly(viewOrder)
                R.id.nav_order_checkout -> showOnly(viewCheckOut)
            }
            true
        }

        btnSubmit.setOnClickListener {

            optionTableNumberLayout.error = ""
            txtCustomerNameLayout.error = ""

            var isValid = true
            val customer = txtCustomerName.text.toString()

            if(selectedTableNumber === null){
                optionTableNumberLayout.error = "Table number is required"
                isValid = false
            }

            if(customer.isEmpty()){
                txtCustomerNameLayout.error = "Customer name is required"
                isValid = false
            }

            if (isValid) {
                showConfirmationDialog()
            }
        }

        chipAppetizer.setOnClickListener {
            filtered = "Appetizer"
            chipAppetizer.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(selectedColor))
            chipMainCourse.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C71585"))
            chipDessert.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#808000"))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#2F4F4F"))
            val filtered = menuOriginals.filter { it.category == "Appetizer" }
            recyclerProduct.adapter = MenuOrderAdapter(filtered) { item -> handleClicked(item) }
        }

        chipMainCourse.setOnClickListener {
            filtered = "Main Course"
            chipAppetizer.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#0000CD"))
            chipMainCourse.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(selectedColor))
            chipDessert.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#808000"))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#2F4F4F"))
            val filtered = menuOriginals.filter { it.category == "Main Course" }
            recyclerProduct.adapter = MenuOrderAdapter(filtered) { item -> handleClicked(item) }
        }

        chipDessert.setOnClickListener {
            filtered = "Dessert"
            chipAppetizer.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#0000CD"))
            chipMainCourse.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C71585"))
            chipDessert.chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(selectedColor))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#2F4F4F"))
            val filtered = menuOriginals.filter { it.category == "Dessert" }
            recyclerProduct.adapter = MenuOrderAdapter(filtered) { item -> handleClicked(item) }
        }

        chipAll.setOnClickListener {
            filtered = "All"
            chipAppetizer.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#0000CD"))
            chipMainCourse.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#C71585"))
            chipDessert.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#808000"))
            chipAll.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(selectedColor))
            recyclerProduct.adapter =
                MenuOrderAdapter(menuOriginals) { item -> handleClicked(item) }
        }

        searchEditText.addTextChangedListener { editable ->
            val keyword = editable.toString().lowercase()
            val result = menuOriginals.filter {
                it.name.lowercase().contains(keyword) ||
                        it.category.lowercase().contains(keyword)
            }
            recyclerProduct.adapter = MenuOrderAdapter(result) { item -> handleClicked(item) }
        }

        optionTableNumber.setOnItemClickListener { _, _, position, _ ->
            var selected = tables[position]
            selectedTableNumber = selected.name
        }

        bottomNav.selectedItemId = R.id.nav_order_checkout

        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rootView.viewTreeObserver.removeOnPreDrawListener(this)
                loadData(this@UpdateOrderActivity, id)
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

            val optionTableNumber = findViewById<AutoCompleteTextView>(R.id.optionTableNumber)
            val txtOrderNumber = findViewById<TextInputEditText>(R.id.txtOrderNumber)
            val txtCustomerName = findViewById<TextInputEditText>(R.id.txtCustomerName)
            val recyclerProduct = findViewById<RecyclerView>(R.id.recyclerProduct)
            val shimmerLoader = findViewById<ShimmerFrameLayout>(R.id.shimmerContent)
            val mainContent = findViewById<ScrollView>(R.id.mainContent)
            val client = AppHelper.getHttpClient()
            val request = AppHelper.getHttpRequest(context, "api/order/detail/$id")

            recyclerProduct.layoutManager = LinearLayoutManager(context)
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

                            val optTable = findViewById<AutoCompleteTextView>(R.id.optionTableNumber)
                            val recyclerOrderDetail = findViewById<RecyclerView>(R.id.recyclerOrderDetail)
                            val responseBody = response.body?.string()
                            val data = Gson().fromJson(responseBody, OrderInitResponse::class.java)
                            val tablesData = data.tables.map { it.name }
                            val tableNumberAdapterOption = ArrayAdapter(
                                this@UpdateOrderActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                tablesData
                            )
                            tables = data.tables
                            cart = data.cart.toMutableList()
                            optionTableNumber.setAdapter(tableNumberAdapterOption)
                            recyclerProduct.adapter =
                                MenuOrderAdapter(data.menus) { item -> handleClicked(item) }
                            menuOriginals = data.menus
                            orderNumber = data.order.order_number
                            txtOrderNumber.setText(data.order.order_number)
                            txtCustomerName.setText(data.order.customer_name)

                            recyclerOrderDetail.adapter = MenuOrderHandleAdapter(
                                items = cart,
                                onAddItem = { item -> onAddItem(item) },
                                onLessItem = { item -> onLessItem(item) },
                                onRemoveItem = { item -> onRemoveItem(item) }
                            )

                            calculateAll()
                            optTable.setText(data.order.table_number, false)
                            selectedTableNumber = data.order.table_number
                            shimmerLoader.stopShimmer()
                            shimmerLoader.visibility = View.GONE
                            mainContent.visibility = View.VISIBLE
                        }, 2000)
                    }
                }

            })


        } catch (e: Exception) {
            println("Something went wrong : ${e.message}")
        }
    }

    private fun handleClicked(menu: ProductResponse) {
        val recyclerOrderDetail = findViewById<RecyclerView>(R.id.recyclerOrderDetail)
        val newCart = CartResponse(
            orderNumber,
            menu.image,
            menu.name,
            1,
            menu.price,
            menu.price
        )
        val checkMenu = cart.filter { it.menu_name == menu.name }
        if (checkMenu.isEmpty()) {
            val currentCart = cart
            currentCart.add(newCart)
        } else {
            val currentCart = cart
            val index = currentCart.indexOfFirst { it.menu_name == menu.name }
            if (index != -1) {
                val newQty = currentCart[index].qty.toInt() + 1
                val newTotal = currentCart[index].total.toDouble() * newQty
                currentCart[index].qty = newQty
                currentCart[index].total = newTotal
            }
            cart = currentCart
        }

        recyclerOrderDetail.adapter = MenuOrderHandleAdapter(
            items = cart,
            onAddItem = { item -> onAddItem(item) },
            onLessItem = { item -> onLessItem(item) },
            onRemoveItem = { item -> onRemoveItem(item) }
        )

        calculateAll()
    }

    private fun onAddItem(item: CartResponse) {
        try {
            val recyclerOrderDetail = findViewById<RecyclerView>(R.id.recyclerOrderDetail)
            val currentCart = cart
            val index = currentCart.indexOfFirst { it.menu_name == item.menu_name }
            if (index != -1) {
                val newQty = currentCart[index].qty.toInt() + 1
                val newTotal = currentCart[index].price.toDouble() * newQty
                currentCart[index].qty = newQty
                currentCart[index].total = newTotal
            }
            cart = currentCart
            recyclerOrderDetail.adapter = MenuOrderHandleAdapter(
                items = cart,
                onAddItem = { item -> onAddItem(item) },
                onLessItem = { item -> onLessItem(item) },
                onRemoveItem = { item -> onRemoveItem(item) }
            )
            calculateAll()
        } catch (e: Exception) {
            println("Error on AddItem : ${e.message}")
        }
    }

    private fun onLessItem(item: CartResponse) {
        try {
            val recyclerOrderDetail = findViewById<RecyclerView>(R.id.recyclerOrderDetail)
            val currentCart = cart
            val index =
                currentCart.indexOfFirst { it.menu_name == item.menu_name && it.qty.toInt() > 1 }
            if (index != -1) {
                val newQty = currentCart[index].qty.toInt() - 1
                val newTotal = currentCart[index].price.toDouble() * newQty
                currentCart[index].qty = newQty
                currentCart[index].total = newTotal
            }
            cart = currentCart
            recyclerOrderDetail.adapter = MenuOrderHandleAdapter(
                items = cart,
                onAddItem = { item -> onAddItem(item) },
                onLessItem = { item -> onLessItem(item) },
                onRemoveItem = { item -> onRemoveItem(item) }
            )
            calculateAll()
        } catch (e: Exception) {
            println("Error on onLessItem : ${e.message}")
        }
    }

    private fun onRemoveItem(item: CartResponse) {
        try {
            val recyclerOrderDetail = findViewById<RecyclerView>(R.id.recyclerOrderDetail)
            val currentCart = cart
            val index = currentCart.indexOfFirst { it.menu_name == item.menu_name }
            currentCart.removeAt(index)
            cart = currentCart
            recyclerOrderDetail.adapter = MenuOrderHandleAdapter(
                items = cart,
                onAddItem = { item -> onAddItem(item) },
                onLessItem = { item -> onLessItem(item) },
                onRemoveItem = { item -> onRemoveItem(item) }
            )
            calculateAll()
        } catch (e: Exception) {
            println("Error on onRemoveItem : ${e.message}")
        }
    }

    private fun calculateAll() {
        var totalItem: Number = 0
        var totalAll: Number = 0
        val currentCart = cart
        val txtTotalPaidItem = findViewById<TextView>(R.id.txtTotalPaidItem)
        val txtTotalPayment = findViewById<TextView>(R.id.txtTotalPayment)
        val txtTotalItem = findViewById<TextView>(R.id.txtTotalItem)
        val btnSubmit = findViewById<TextView>(R.id.btnSubmit)

        currentCart.forEach {
            totalAll = totalAll.toDouble() + it.total.toDouble()
            totalItem = totalItem.toInt() + it.qty.toInt()
        }

        if (currentCart.isEmpty()) {
            btnSubmit.isEnabled = false
            txtTotalPaidItem.visibility = View.GONE
            txtTotalPayment.text = "0.00"
            txtTotalItem.text = totalItem.toString()
        } else {
            btnSubmit.isEnabled = true
            txtTotalPaidItem.text = "TOTAL PAID  \$ ${String.format("%.2f", totalAll.toDouble())}"
            txtTotalPayment.text = "${String.format("%.2f", totalAll.toDouble())}"
            txtTotalPaidItem.visibility = View.VISIBLE
            txtTotalItem.text = totalItem.toString()
        }

    }

    private fun showOnly(viewToShow: View) {
        val allViews = listOf(viewMenu, viewOrder, viewCheckOut)
        allViews.forEach { it.visibility = if (it == viewToShow) View.VISIBLE else View.GONE }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this@UpdateOrderActivity)
            .setTitle("Checkout Confirmation")
            .setMessage("Do you want to confirm this action ?")
            .setPositiveButton("Yes, Continue") { dialog, _ ->
                dialog.dismiss()
                onSubmit()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun onSubmit() {
        try {

            val context = this@UpdateOrderActivity
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false) // optional
                .create()
            dialog.show()

            val checkout = 1
            val order_number = findViewById<TextInputEditText>(R.id.txtOrderNumber).text.toString()
            val customer_name = findViewById<TextInputEditText>(R.id.txtCustomerName).text.toString()
            val order_type = "Dine In"
            val status =  1
            val cart = cart
            val table_number =  selectedTableNumber
            val total_paid = findViewById<TextInputEditText>(R.id.txtTotalItem).text.toString()

            val formData = OrderRequest(
                checkout,
                order_number,
                customer_name,
                order_type.toString(),
                status,
                cart,
                table_number.toString(),
                total_paid.toDouble()
            )

            val client = AppHelper.getHttpClient()
            val request = AppHelper.postHttpRequest(context, "api/order/save", formData)

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    (context as Activity).runOnUiThread{
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()
                            val responseBody = response.body?.string()
                            if(response.code == 200){
                                val resultResponse = Gson().fromJson(responseBody, SuccessResponse::class.java)
                                val intent = Intent(context, MainAppActivity::class.java)
                                AppHelper.showToast(context, resultResponse.message, false)
                                intent.putExtra("tabActive", "history")
                                startActivity(intent)
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

        } catch (e: Exception) {
            println("Something went wrong : ${e.message}")
        }
    }
}