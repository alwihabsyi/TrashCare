package com.upnvjt.trashcare.ui.main.tacycle.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.upnvjt.trashcare.databinding.ActivityTaCycleCartBinding
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

        setUpTacycleTabLayout()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpTacycleTabLayout() {
        val statusFragment = arrayListOf(
            OnGoingFragment(),
            DoneFragment()
        )

        binding.viewPager2.isUserInputEnabled = false

        val viewPagerAdapter = TaCycleViewPagerAdapter(statusFragment, supportFragmentManager, lifecycle)
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
}