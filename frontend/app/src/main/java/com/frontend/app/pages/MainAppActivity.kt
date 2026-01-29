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

package com.frontend.app.pages

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.frontend.app.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.frontend.app.fragments.HistoryFragment
import com.frontend.app.fragments.HomeFragment
import com.frontend.app.fragments.MenuFragment
import com.frontend.app.fragments.ProfileFragment
import com.frontend.app.fragments.OrderFragment

  class MainAppActivity : AppCompatActivity() {

      private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main_app)

        val tabActive = intent.getStringExtra("tabActive").toString()
        bottomNav = findViewById(R.id.bottomNavigationView)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, "Home")
                        .commit()
                    true
                }

                R.id.nav_history -> {
                    val fragment = HistoryFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, "History")
                        .commit()
                    true
                }

                R.id.nav_menu -> {
                    val fragment = MenuFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, "Menu")
                        .commit()
                    true
                }

                R.id.nav_order -> {
                    val fragment = OrderFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, "Order")
                        .commit()
                    true
                }

                R.id.nav_profile -> {
                    val fragment = ProfileFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment, "Profile")
                        .commit()
                    true
                }

                else -> false
            }
        }

        if(tabActive.isEmpty()){
            bottomNav.selectedItemId = R.id.nav_home
        }else{
           if(tabActive == "home"){
               bottomNav.selectedItemId = R.id.nav_home
           }else if(tabActive == "history"){
               bottomNav.selectedItemId = R.id.nav_history
           }else if(tabActive == "menu"){
               bottomNav.selectedItemId = R.id.nav_menu
           }else if(tabActive == "order"){
               bottomNav.selectedItemId = R.id.nav_order
           }else if(tabActive == "profile"){
               bottomNav.selectedItemId = R.id.nav_profile
           }else{
               bottomNav.selectedItemId = R.id.nav_home
           }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}