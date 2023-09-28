package com.upnvjt.trashcare.ui.main.commerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.data.tacommerce.TaCommerceAdapter
import com.upnvjt.trashcare.databinding.FragmentCommerceBinding
import com.upnvjt.trashcare.ui.main.home.viewmodel.HomeViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.filterProductDialog
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.onTextSubmit
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.showBottomNavView
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommerceFragment : Fragment() {

    private var _binding: FragmentCommerceBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val productAdapter by lazy { TaCommerceAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommerceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        setUpRv()
        observer()
    }

    private fun setupAction() {
        binding.apply {
            btnMyCart.setOnClickListener {
                findNavController().navigate(R.id.action_commerceFragment_to_myCartFragment)
            }

            searchView.onTextSubmit {
                searchView.setQuery("", false)
                val b = Bundle().apply { putString(SEARCH_QUERY, it) }
                findNavController().navigate(R.id.action_commerceFragment_to_searchFragment, b)
            }

            btnFilter.setOnClickListener {
                filterProductDialog(
                    hargaTerendah = { viewModel.getFilteredProducts(true) },
                    hargaTertinggi = { viewModel.getFilteredProducts(false) }
                )
            }
        }
    }

    private fun observer() {
        viewModel.allProducts.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                }

                is State.Success -> {
                    binding.progressBar.hide()
                    setUpRvData(it.data!!)
                }

                is State.Error -> {
                    binding.progressBar.hide()
                    toast(it.message.toString())
                }
            }
        }

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.getAllProducts()
            }
        })
    }

    private fun setUpRvData(data: List<Product>) {
        productAdapter.differ.submitList(data)
    }

    private fun setUpRv() {
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        showBottomNavView()
    }

    companion object {
        const val SEARCH_QUERY = "item_name"
    }
}