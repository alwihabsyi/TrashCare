package com.upnvjt.trashcare.ui.main.commerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjt.trashcare.data.tacommerce.Cart
import com.upnvjt.trashcare.data.tacommerce.CartAdapter
import com.upnvjt.trashcare.databinding.FragmentMyCartBinding
import com.upnvjt.trashcare.ui.main.commerce.viewmodel.CartViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.hideBottomNavView
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCartFragment : Fragment() {

    private var _binding: FragmentMyCartBinding? = null
    private val binding get() = _binding!!
    private val cartAdapter by lazy { CartAdapter() }
    private val viewModel by viewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        _binding = FragmentMyCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPage()
        observer()
    }

    private fun setupPage() {
        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observer() {
        viewModel.cart.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    setupRvData(it.data!!)
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun setupRvData(data: List<Cart>) {
        cartAdapter.differ.submitList(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}