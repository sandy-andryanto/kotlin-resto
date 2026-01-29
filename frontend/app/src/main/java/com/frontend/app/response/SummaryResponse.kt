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

data class SummaryResponse(
    val total_sales: Number,
    val total_orders: Number,
    val total_dine_in: Number,
    val total_take_away: Number,
    val tables: List<TableResponse>,
    val products: List<ProductResponse>,
)