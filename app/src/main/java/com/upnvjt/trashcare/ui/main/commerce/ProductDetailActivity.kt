package com.upnvjt.trashcare.ui.main.commerce

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.databinding.ActivityProductDetailBinding
import com.upnvjt.trashcare.ui.main.commerce.viewmodel.ProductDetailViewModel
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.glide
import com.upnvjt.trashcare.util.toPrice
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private var _binding: ActivityProductDetailBinding? = null
    private val binding get() = _binding!!
    private var orderAmount: Int = 1
    private var product: Product? = null
    private val viewModel by viewModels<ProductDetailViewModel>()
    private var isBookmarked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPage()
        setActions()
        observer()
    }

    @Suppress("DEPRECATION")
    private fun setupPage() {
        product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Constants.PRODUCTS, Product::class.java)
        } else {
            intent.getParcelableExtra(Constants.PRODUCTS)
        }

        product?.let {
            viewModel.getBookmark(it.id)
            binding.apply {
                tvProductTitle.text = it.name
                tvPriceProduct.text = it.price.toPrice()
                tvDescriptionProduct.text = it.description
                imgDetailProduct.glide(it.photoUrl)
            }
        }
    }

    private fun setActions() {
        binding.apply {
            tvAmount.text = orderAmount.toString()

            btnIncreaseItem.setOnClickListener {
                product?.let {
                    if (orderAmount < it.stock) {
                        orderAmount += 1
                        binding.tvAmount.text = orderAmount.toString()
                    }
                }
            }

            btnDecreaseItem.setOnClickListener {
                if (orderAmount > 1) {
                    orderAmount -= 1
                    tvAmount.text = orderAmount.toString()
                }
            }

            btnAddBookmark.setOnClickListener {
                product?.let {
                    viewModel.bookmarkProduct(it, isBookmarked)
                }
            }
        }
    }

    private fun observer() {
        viewModel.getBookmark.observe(this) {
            when (it) {
                is State.Success -> {
                    it.data?.let { exists ->
                        isBookmarked = if (exists) {
                            binding.btnAddBookmark.setIconResource(R.drawable.ic_bookmarked)
                            binding.btnAddBookmark.setIconTintResource(R.color.yellow)
                            true
                        } else {
                            binding.btnAddBookmark.setIconResource(R.drawable.baseline_bookmark_border_24)
                            binding.btnAddBookmark.setIconTintResource(R.color.black)
                            false
                        }
                    }
                }

                is State.Error -> {
                    toast(it.message.toString())
                }

                else -> {}
            }
        }

        viewModel.bookmarkProduct.observe(this) {
            when (it) {
                is State.Error -> {
                    toast(it.message.toString())
                }

                else -> {}
            }
        }
    }
}