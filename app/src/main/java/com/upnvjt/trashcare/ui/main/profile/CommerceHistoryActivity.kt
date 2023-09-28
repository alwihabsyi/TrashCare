package com.upnvjt.trashcare.ui.main.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.upnvjt.trashcare.databinding.ActivityCommerceHistoryBinding
import com.upnvjt.trashcare.data.ViewPagerAdapter
import com.upnvjt.trashcare.ui.main.commerce.cart.CommerceDeclinedFragment
import com.upnvjt.trashcare.ui.main.commerce.cart.CommerceDoneFragment
import com.upnvjt.trashcare.ui.main.commerce.cart.CommerceOnGoingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommerceHistoryActivity : AppCompatActivity() {

    private var _binding: ActivityCommerceHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommerceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpTacycleTabLayout()
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpTacycleTabLayout() {
        val statusFragment = arrayListOf(
            CommerceOnGoingFragment(),
            CommerceDoneFragment(),
            CommerceDeclinedFragment()
        )

        binding.viewPager2.isUserInputEnabled = false

        val viewPagerAdapter =
            ViewPagerAdapter(statusFragment, supportFragmentManager, lifecycle)
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "OnGoing"
                1 -> tab.text = "Done"
                2 -> tab.text = "Declined"
            }
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}