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

package com.frontend.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.frontend.app.R
import com.frontend.app.response.CartResponse


class MenuOrderHandleAdapter(
    private val items: List<CartResponse>,
    private val onAddItem: (CartResponse) -> Unit,
    private val onLessItem: (CartResponse) -> Unit,
    private val onRemoveItem: (CartResponse) -> Unit
) :
    RecyclerView.Adapter<MenuOrderHandleAdapter.MenuOrderHandleViewHolder>() {

    inner class MenuOrderHandleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val btnAdd: Button = view.findViewById(R.id.btnAdd)
        private val btnLess: Button = view.findViewById(R.id.btnLess)
        private val btnRemove: Button = view.findViewById(R.id.btnRemove)

        fun bind(product: CartResponse) {
            btnAdd.setOnClickListener {
                onAddItem(product)
            }
            btnLess.setOnClickListener {
                onLessItem(product)
            }
            btnRemove.setOnClickListener {
                onRemoveItem(product)
            }
        }

        val image = view.findViewById<ImageView>(R.id.menu_image)
        val name = view.findViewById<TextView>(R.id.menu_name)
        val price = view.findViewById<TextView>(R.id.menu_price)
        val total = view.findViewById<TextView>(R.id.menu_total)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuOrderHandleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_handle, parent, false)
        return MenuOrderHandleViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuOrderHandleViewHolder, position: Int) {
        val product = items[position]
        Glide.with(holder.itemView.context).load(product.menu_image).into(holder.image)
        holder.bind(items[position])
        holder.name.text = product.menu_name
        holder.price.text = "\$ ${product.price.toString()} x ${product.qty} (pcs)"
        holder.total.text = "\$ ${String.format("%.2f", product.total.toDouble())}"
    }

    override fun getItemCount(): Int = items.size

}