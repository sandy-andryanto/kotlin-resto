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

package com.frontend.app.requests

import com.frontend.app.response.CartResponse

data class OrderRequest (
    val checkout: Number,
    val order_number: String,
    val customer_name: String,
    val order_type: String,
    val status: Number,
    val cart: MutableList<CartResponse>,
    val table_number: String,
    val total_paid: Number,
)
