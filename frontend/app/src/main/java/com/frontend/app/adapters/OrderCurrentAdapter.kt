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

package com.frontend.app.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frontend.app.R
import com.frontend.app.helpers.AppHelper
import com.frontend.app.pages.DetailOrderActivity
import com.frontend.app.pages.UpdateOrderActivity
import com.frontend.app.response.OrderResponse

class OrderCurrentAdapter(private val items: List<OrderResponse>) :

    RecyclerView.Adapter<OrderCurrentAdapter.OrderCurrentViewHolder>() {

    class OrderCurrentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderNumber = view.findViewById<TextView>(R.id.txtOrderId)
        val orderDate = view.findViewById<TextView>(R.id.txtOrderDate)
        val tableNumber = view.findViewById<TextView>(R.id.txtTableNumber)
        val orderType = view.findViewById<TextView>(R.id.txtOrderType)
        val customerName = view.findViewById<TextView>(R.id.txtCustomerName)
        val cashierName = view.findViewById<TextView>(R.id.txtCasheirName)
        val totalItem = view.findViewById<TextView>(R.id.txtTotalItem)
        val totalPaid = view.findViewById<TextView>(R.id.txtTotalPaid)
        val btnViewOrder = view.findViewById<Button>(R.id.btnViewOrder)
        val btnCheckOut = view.findViewById<Button>(R.id.btnCheckOut)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCurrentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_current, parent, false)
        return OrderCurrentViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderCurrentViewHolder, position: Int) {
        try{

            val order = items[position]
            holder.orderNumber.text = order.order_number
            holder.orderDate.text = "${AppHelper.formatWithOrdinal(order.created_at)}"
            holder.tableNumber.text =  if (order.order_type == "Dine In") order.table_number else "-"
            holder.orderType.text = order.order_type
            holder.customerName.text = order.customer_name
            holder.cashierName.text = order.cashier_name
            holder.totalItem.text = "${order.total_item.toString()} pcs"
            holder.totalPaid.text = "\$ ${String.format("%.2f", order.total_paid.toDouble())}"

            holder.btnViewOrder.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, DetailOrderActivity::class.java)
                intent.putExtra("id", order._id)
                context.startActivity(intent)
            }

            holder.btnCheckOut.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, UpdateOrderActivity::class.java)
                intent.putExtra("id", order._id)
                context.startActivity(intent)
            }

        }catch (e: Exception) {
            println("Error on : ${e.message}")
        }
    }

    override fun getItemCount(): Int = items.size

}