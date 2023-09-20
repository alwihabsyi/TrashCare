package com.upnvjt.trashcare.ui.main.commerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.upnvjt.trashcare.databinding.FragmentCommerceBinding

class CommerceFragment : Fragment() {

    private lateinit var binding:FragmentCommerceBinding
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =FragmentCommerceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.tabLayoutCommerce

        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Furniture"))
        tabLayout.addTab(tabLayout.newTab().setText("Ceramics"))
        tabLayout.addTab(tabLayout.newTab().setText("Bag"))
        tabLayout.addTab(tabLayout.newTab().setText("Fertilizer"))
        tabLayout.addTab(tabLayout.newTab().setText("Collection & Toy"))
        tabLayout.addTab(tabLayout.newTab().setText("Others"))

    }

}