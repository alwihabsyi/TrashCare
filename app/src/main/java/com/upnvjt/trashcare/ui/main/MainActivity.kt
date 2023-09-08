package com.upnvjt.trashcare.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.upnvjt.trashcare.databinding.ActivityMainBinding
import com.upnvjt.trashcare.ui.main.tacycle.TaCycleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.background = null
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentLayout.id) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

//        binding.taCycleButton.setOnClickListener {
//            startActivity(Intent(this, TaCycleActivity::class.java))
//        }

//        binding.bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> loadFragment(HomeFragment())
//                R.id.home -> loadFragment(HomeFragment())
//                R.id.home -> loadFragment(HomeFragment())
//                R.id.home -> loadFragment(HomeFragment())
//            }
//
//            return@setOnItemSelectedListener true
//
//        }

    }

    fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentLayout.id, fragment)
        fragmentTransaction.commit()
    }
}