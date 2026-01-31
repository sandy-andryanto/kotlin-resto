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

package com.frontend.app.pages

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.frontend.app.R
import com.airbnb.lottie.LottieAnimationView

class DisconnectActivity : AppCompatActivity() {

    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_disconnect)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun loadCustomAnimation(fileName: String) {
        val assetPath = "lottie/$fileName.json"
        lottieAnimationView.setAnimation(assetPath)
        lottieAnimationView.playAnimation()
    }
}