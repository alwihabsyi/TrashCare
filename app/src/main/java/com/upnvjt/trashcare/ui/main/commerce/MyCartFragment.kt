package com.upnvjt.trashcare.ui.main.commerce

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjt.trashcare.data.tacommerce.Cart
import com.upnvjt.trashcare.data.tacommerce.CartAdapter
import com.upnvjt.trashcare.data.tacommerce.OrderStatus
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.databinding.FragmentMyCartBinding
import com.upnvjt.trashcare.ui.main.commerce.CheckoutActivity.Companion.ORDER
import com.upnvjt.trashcare.ui.main.commerce.viewmodel.CartViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.hideBottomNavView
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toPrice
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCartFragment : Fragment() {

    private var _binding: FragmentMyCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CartViewModel>()
    private val listProduct: MutableList<Cart> = mutableListOf()
    private var cartAdapter: CartAdapter = CartAdapter()
    private var total: Double = 0.0
    private var setAll: Boolean = false

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
        setActions()
    }

    private fun setupPage() {
        binding.apply {
            rvCart.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            tvTotalPrice.text = total.toPrice()
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

        viewModel.deleteDialog.observe(viewLifecycleOwner) {
            val alertDialog = AlertDialog.Builder(requireContext()).apply {
                setTitle("Hapus produk dari keranjang")
                setMessage("Apakah anda yakin ingin menghapus barang ini dari keranjang?")
                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton("Ya") { dialog, _ ->
                    viewModel.deleteCartProduct(it.product.id)
                    dialog.dismiss()
                }
            }
            alertDialog.create().show()
        }

        viewModel.itemQuantity.observe(viewLifecycleOwner) {
            when (it) {
                is State.Error -> {
                    toast(it.message.toString())
                }

                else -> {}
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRvData(data: List<Cart>) {
        cartAdapter = CartAdapter()
        binding.rvCart.adapter = cartAdapter
        cartAdapter.differ.submitList(data)

        setupCheckBox(data)
        cartAdapter.onPlusClick = {
            if (listProduct.contains(it)) {
                listProduct.remove(it)
                checkTotalPrice()
            }
            viewModel.changeQuantity(it, true)
        }

        cartAdapter.onMinusClick = {
            if (listProduct.contains(it)) {
                listProduct.remove(it)
                checkTotalPrice()
            }
            viewModel.changeQuantity(it, false)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCheckBox(data: List<Cart>) {
        binding.checkbox.setOnClickListener {
            if (binding.checkbox.isChecked) {
                listProduct.clear()
                data.forEach {
                    if (!listProduct.contains(it)) {
                        listProduct.add(it)
                        checkTotalPrice()
                    }
                }
                cartAdapter.allSelected(true)
            } else {
                listProduct.clear()
                checkTotalPrice()
                cartAdapter.allSelected(false)
            }
        }

        cartAdapter.check = { item, checkBox ->
            checkBox.isChecked = listProduct.contains(item)
            if (!setAll) {
                checkBox.setOnClickListener {
                    if (checkBox.isChecked) {
                        listProduct.add(item)
                        checkTotalPrice()
                    } else {
                        listProduct.remove(item)
                        checkTotalPrice()
                    }
                }
            }
        }
    }

    private fun setActions() {
        binding.btnCheckOut.setOnClickListener {
            if (listProduct.isEmpty() || total == 0.0) {
                toast("Tidak ada barang dipilih")
                return@setOnClickListener
            }

            val order = Orders(
                OrderStatus.OnGoing.orderStatus,
                total,
                listProduct,
                UserAddress()
            )

            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            intent.putExtra(ORDER, order)
            startActivity(intent)
        }
    }

    private fun checkTotalPrice() {
        total = 0.0
        listProduct.forEach {
            total += it.quantity * it.product.price
        }
        binding.tvTotalPrice.text = total.toPrice()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}