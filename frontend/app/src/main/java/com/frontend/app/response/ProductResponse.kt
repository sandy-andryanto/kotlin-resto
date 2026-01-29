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

data class ProductResponse (
    val image: String,
    val name: String,
    val category: String,
    val status: Number,
    val price: Number,
    val rating: Number
)