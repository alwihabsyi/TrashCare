package com.upnvjt.trashcare.ui.main.commerce

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjt.trashcare.data.tacommerce.CheckoutAdapter
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.databinding.ActivityCheckoutBinding
import com.upnvjt.trashcare.ui.main.profile.AddressActivity
import com.upnvjt.trashcare.util.Constants.REQUEST_ADDRESS
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toPrice

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CheckoutActivity : AppCompatActivity() {

    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!
    private val checkoutAdapter by lazy { CheckoutAdapter() }
    private var alamat: UserAddress? = null
    private var order: Orders? = null
    private var taCoin = 0
    private var subTotal = 0.0
    private var shipping = 0.0
    private var total = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCartData()
        setActions()
    }

    private fun setupCartData() {
        val cartData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ORDER, Orders::class.java)
        } else {
            intent.getParcelableExtra(ORDER)
        }

        cartData?.let { orders ->
            order = orders
            subTotal = cartData.totalPrice
            total = subTotal + shipping - taCoin

            binding.apply {
                rvPesanan.apply {
                    adapter = checkoutAdapter
                    layoutManager = LinearLayoutManager(this@CheckoutActivity, LinearLayoutManager.VERTICAL, false)
                }

                checkoutAdapter.differ.submitList(orders.products)
                setupBillingDetail()
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
                val intent = Intent(this@CheckoutActivity, AddressActivity::class.java)
                intent.putExtra(AddressActivity.CHECKOUT_ADDRESS, true)
                startActivityForResult(intent, REQUEST_ADDRESS)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADDRESS) {
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

    companion object {
        const val ORDER = "order"
    }
}