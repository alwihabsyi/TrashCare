package com.upnvjt.trashcare.ui.main.commerce

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.tacommerce.CheckoutAdapter
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.databinding.FragmentCheckoutBinding
import com.upnvjt.trashcare.ui.main.profile.AddressActivity
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toPrice
import com.upnvjt.trashcare.util.toast

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<CheckoutFragmentArgs>()
    private val checkoutAdapter by lazy { CheckoutAdapter() }
    private var order: Orders? = null
    private var alamat: UserAddress? = null
    private var shippingMethod: String? = null
    private var taCoin = 0
    private var subTotal = 0.0
    private var shipping = 0.0
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        order = args.order
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpData()
        setActions()
    }

    private fun setUpData() {
        order?.let {
            subTotal = it.totalPrice
            total = subTotal + shipping - taCoin

            binding.apply {
                rvPesanan.apply {
                    adapter = checkoutAdapter
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
                checkoutAdapter.differ.submitList(it.products)
                setupBillingDetail()
                setUpSpinner()
                setUpAddressDetail()
            }
        }
    }

    private fun setUpSpinner() {
        val listMetode =  arrayOf("TaRider")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listMetode)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMetodePengiriman.adapter = spinnerAdapter
    }

    private fun setUpAddressDetail() {
        binding.apply {
            alamat?.let { address ->
                tvPilihAlamat.hide()
                linearAlamat.show()

                tvAlamatUser.text = address.judulAlamat
                tvKodePos.text = "ID ${address.kodePos}"
                tvDetailAlamat.text =
                    "${address.namaJalan}, ${address.kelurahan}, ${address.kecamatan}, ${address.kota}, ${address.provinsi}"
            }
        }
    }

    private fun setupBillingDetail() {
        binding.apply {
            tvTacoinAmount.text = taCoin.toString()
            tvSubtotal.text = subTotal.toPrice()
            tvShipping.text = shipping.toPrice()
            tvTotal.text = total.toPrice()
        }
    }

    private fun setActions() {
        binding.apply {
            layoutAlamat.setOnClickListener {
                val intent = Intent(requireContext(), AddressActivity::class.java)
                intent.putExtra(AddressActivity.CHECKOUT_ADDRESS, true)
                startActivityForResult(intent, Constants.REQUEST_ADDRESS)
            }

            shippingMethod = spinnerMetodePengiriman.selectedItem.toString()

            btnBayar.setOnClickListener {
                if (alamat == null || shippingMethod == null || order == null) {
                    toast("Harap pilih alamat dan metode pengiriman")
                    return@setOnClickListener
                }

                val orders = order?.copy(totalPrice = total, address = alamat!!, shippingMethod = shippingMethod!!)
                val b = Bundle().apply { putParcelable(MyCartFragment.ORDER, orders) }
                findNavController().navigate(R.id.action_checkoutFragment_to_paymentFragment, b)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ADDRESS) {
            val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra(AddressActivity.ADDRESS_PICKED, UserAddress::class.java)
            } else {
                data?.getParcelableExtra(AddressActivity.ADDRESS_PICKED)
            }

            address?.let {
                alamat = address
                binding.apply {
                    tvPilihAlamat.hide()
                    linearAlamat.show()

                    tvAlamatUser.text = it.judulAlamat
                    tvKodePos.text = "ID ${it.kodePos}"
                    tvDetailAlamat.text =
                        "${it.namaJalan}, ${it.kelurahan}, ${it.kecamatan}, ${it.kota}, ${it.provinsi}"
                }
            }
        }
    }
}