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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.frontend.app.R
import com.frontend.app.response.CartResponse


class CartAdapter(private val items: List<CartResponse>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.menu_image)
        val name = view.findViewById<TextView>(R.id.menu_name)
        val price = view.findViewById<TextView>(R.id.menu_price)
        val total = view.findViewById<TextView>(R.id.menu_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = items[position]
        Glide.with(holder.itemView.context).load(cart.menu_image).into(holder.image)
        holder.name.text = cart.menu_name
        holder.price.text = "\$ ${String.format("%.2f", cart.price.toDouble())} x ${cart.qty}"
        holder.total.text = "\$ ${String.format("%.2f", cart.total.toDouble())}"
    }

    override fun getItemCount(): Int = items.size

}
