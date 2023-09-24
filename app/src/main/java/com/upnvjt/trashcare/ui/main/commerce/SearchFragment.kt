package com.upnvjt.trashcare.ui.main.commerce

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.data.tacommerce.TaCommerceAdapter
import com.upnvjt.trashcare.databinding.FragmentSearchBinding
import com.upnvjt.trashcare.ui.main.commerce.CommerceFragment.Companion.SEARCH_QUERY
import com.upnvjt.trashcare.ui.main.commerce.viewmodel.TaCommerceViewModel
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.hideBottomNavView
import com.upnvjt.trashcare.util.onTextSubmit
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SearchFragmentArgs>()
    private val viewModel by viewModels<TaCommerceViewModel>()
    private var query: String? = null
    private val productAdapter by lazy { TaCommerceAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = args.itemName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavView()
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPage()
        setUpRv()
        observer()
    }

    private fun setPage() {
        binding.apply {
            query?.let {
                searchView.setQuery(it, false)
                viewModel.getSearchedProducts(query!!)
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            searchView.onTextSubmit {
                val b = Bundle().apply { putString(SEARCH_QUERY, it) }
                findNavController().navigate(R.id.action_searchFragment_self, b)
            }
        }
    }

    private fun observer() {
        viewModel.searchProducts.observe(viewLifecycleOwner) {
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
    }

    private fun setUpRvData(data: List<Product>) {
        productAdapter.differ.submitList(data)
        productAdapter.onClick = {
            Intent(requireContext(), ProductDetailActivity::class.java).apply {
                putExtra(Constants.PRODUCTS, it)
                startActivity(this)
            }
        }
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

}