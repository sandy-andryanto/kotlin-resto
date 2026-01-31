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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.frontend.app.R
import com.frontend.app.response.ProductResponse


class MenuAdapter(private val items: List<ProductResponse>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.menu_image)
        val category = view.findViewById<TextView>(R.id.menu_category)
        val name = view.findViewById<TextView>(R.id.menu_name)
        val price = view.findViewById<TextView>(R.id.menu_price)
        val starContainer: LinearLayout = view.findViewById(R.id.starContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val product = items[position]
        Glide.with(holder.itemView.context).load(product.image).into(holder.image)
        holder.category.text = product.category
        holder.name.text = product.name
        holder.price.text = "\$ ${product.price.toString()}"
        renderStars(holder.starContainer, product.rating.toInt())
    }

    private fun renderStars(container: LinearLayout, currentRating: Int) {
        container.removeAllViews()

        for (i in 1..10) {
            val star = ImageView(container.context).apply {
                val drawableId =
                    if (i <= currentRating) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                setImageResource(drawableId)
                val size = 48 // px or use dp conversion
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(4, 0, 4, 0)
                }
            }
            container.addView(star)
        }
    }

    override fun getItemCount(): Int = items.size

}
