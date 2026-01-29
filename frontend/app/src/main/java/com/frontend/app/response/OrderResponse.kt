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

package com.frontend.app.response

import java.util.Date


data class OrderResponse(
    val _id: String,
    val order_number: String,
    val table_number: String,
    val order_type: String,
    val customer_name: String,
    val cashier_name: String,
    val total_item: Number,
    val total_paid: Number,
    val status: Number,
    val created_at: Date,
    val updated_at: Date
)