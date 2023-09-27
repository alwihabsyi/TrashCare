package com.upnvjt.trashcare.ui.main.commerce.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.CommerceHistoryAdapter
import com.upnvjt.trashcare.data.tacommerce.OrderStatus
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.databinding.FragmentDoneBinding
import com.upnvjt.trashcare.ui.main.tacycle.viewmodel.OrderViewModel
import com.upnvjt.trashcare.ui.main.tacycle.viewmodel.OrderViewModelFactory
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommerceDoneFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var auth: FirebaseAuth

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!
    private val cartAdapter by lazy { CommerceHistoryAdapter() }
    private val viewModel by viewModels<OrderViewModel> {
        OrderViewModelFactory(
            firestore,
            auth,
            TaCycleStatus.Done,
            OrderStatus.Done
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrdersRv()
        observer()
    }

    private fun observer() {
        viewModel.commerceOrders.observe(viewLifecycleOwner) {
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
                    toast(it.message!!)
                }
            }
        }
    }

    private fun setupRvData(data: List<Orders>) {
        cartAdapter.differ.submitList(data)
        if(data.isEmpty()) {
            binding.tvNoOrder.show()
        }
    }

    private fun setupOrdersRv() {
        viewModel.getCommerceOrders()
        binding.rvDone.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}