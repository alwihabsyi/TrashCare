package com.upnvjt.trashcare.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacampaign.TaskAdapter
import com.upnvjt.trashcare.data.tacommerce.TaCommerceAdapter
import com.upnvjt.trashcare.databinding.FragmentHomeBinding
import com.upnvjt.trashcare.ui.main.home.viewmodel.HomeViewModel
import com.upnvjt.trashcare.ui.main.tacycle.TaCycleActivity
import com.upnvjt.trashcare.ui.main.tacycle.cart.TaCycleCartActivity
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.showBottomNavView
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bannerViewPager: ViewPager2
    private val taskAdapter by lazy { TaskAdapter(true) }
    private val productAdapter by lazy { TaCommerceAdapter(true) }
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        setUpRv()
        setActions()
        observer()
    }

    private fun setUpRv() {
        binding.apply {
            rvTacampaign.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }

            rvListProduct.apply {
                adapter = productAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnTacycleCart.setOnClickListener {
                startActivity(Intent(requireContext(), TaCycleCartActivity::class.java))
            }

            viewPager.setOnClickListener {
                Intent(requireActivity(), TaCycleActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun setViewPager() {
        val images = listOf(
            R.drawable.poster_three,
            R.drawable.poster_one,
            R.drawable.poster_two
        )

        bannerViewPager = binding.viewPager
        val bannerAdapter = BannerAdapter(images)
        bannerViewPager.adapter = bannerAdapter
        val indicator3: CircleIndicator3 = requireView().findViewById(R.id.circleIndicator3)
        indicator3.setViewPager(bannerViewPager)
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        viewModel.task.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.taskProgressBar.show()
                }
                is State.Success -> {
                    binding.taskProgressBar.hide()
                    taskAdapter.differ.submitList(it.data)
                }
                is State.Error -> {
                    binding.taskProgressBar.hide()
                    toast(it.message.toString())
                }
            }
        }

        viewModel.user.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.userProgressBar.show()
                }
                is State.Success -> {
                    binding.userProgressBar.hide()
                    val user = it.data
                    user?.let { data ->
                        val nama = if (data.lastname == null) data.firstname else "${data.firstname} ${data.lastname}"
                        binding.welcomeUser.text = "Hello, $nama"
                        binding.tacoins.text = data.taCoins.toString()
                    }
                }
                is State.Error -> {
                    binding.userProgressBar.hide()
                    toast(it.message.toString())
                }
            }
        }


        viewModel.allProducts.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.productsProgressBar.show()
                }
                is State.Success -> {
                    binding.productsProgressBar.hide()
                    productAdapter.differ.submitList(it.data)
                }
                is State.Error -> {
                    binding.productsProgressBar.hide()
                    toast(it.message.toString())
                }
            }
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
}