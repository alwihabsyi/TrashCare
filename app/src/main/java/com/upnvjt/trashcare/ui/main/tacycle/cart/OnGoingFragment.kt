package com.upnvjt.trashcare.ui.main.tacycle.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.databinding.FragmentOnGoingBinding
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnGoingFragment : Fragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var auth: FirebaseAuth

    private var _binding: FragmentOnGoingBinding? = null
    private val binding get() = _binding!!
    private val cartAdapter by lazy { TacycleCartAdapter() }
    private val viewModel by viewModels<TaCycleOrderViewModel> {
        TaCycleOrderViewModelFactory(
            firestore,
            auth,
            TaCycleStatus.OnGoing
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnGoingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrdersRv()
        observer()
    }

    private fun observer() {
        viewModel.allOrders.observe(viewLifecycleOwner) {
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

    private fun setupRvData(data: List<TacycleModel>) {
        cartAdapter.differ.submitList(data)
        if(data.isEmpty()) {
            binding.tvNoOrder.show()
        }
    }

    private fun setupOrdersRv() {
        binding.rvOngoing.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}