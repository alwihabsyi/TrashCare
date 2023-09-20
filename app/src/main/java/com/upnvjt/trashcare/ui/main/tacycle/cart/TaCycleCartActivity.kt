package com.upnvjt.trashcare.ui.main.tacycle.cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.upnvjt.trashcare.databinding.ActivityTaCycleCartBinding
import com.upnvjt.trashcare.ui.main.MainActivity
import com.upnvjt.trashcare.ui.main.tacycle.TaCycleViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaCycleCartActivity : AppCompatActivity() {

    private var _binding: ActivityTaCycleCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaCycleCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setUpTacycleTabLayout()
    }

    private fun setupAction() {
        val fromOrder = intent.getBooleanExtra(FROM_ORDER, false)

        binding.btnBack.setOnClickListener {
            if (fromOrder) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (fromOrder) {
                    val intent = Intent(this@TaCycleCartActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    finish()
                }
            }
        })
    }

    private fun setUpTacycleTabLayout() {
        val statusFragment = arrayListOf(
            OnGoingFragment(),
            DoneFragment()
        )

        binding.viewPager2.isUserInputEnabled = false

        val viewPagerAdapter =
            TaCycleViewPagerAdapter(statusFragment, supportFragmentManager, lifecycle)
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "OnGoing"
                1 -> tab.text = "Done"
            }
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val FROM_ORDER = "tacycle"
    }
}