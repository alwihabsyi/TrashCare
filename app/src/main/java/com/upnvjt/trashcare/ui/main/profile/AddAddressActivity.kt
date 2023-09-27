package com.upnvjt.trashcare.ui.main.profile

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.databinding.ActivityAddAddressBinding
import com.upnvjt.trashcare.ui.main.profile.viewmodel.AddressViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.string
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {

    private var _binding: ActivityAddAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AddressViewModel>()
    private var job: Job? = null
    private var userAddress: UserAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpPage()
        setActions()
        observer()
    }

    @Suppress("DEPRECATION")
    private fun setUpPage() {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EDIT_ADDRESS, UserAddress::class.java)
        } else {
            intent.getParcelableExtra(EDIT_ADDRESS)
        }

        data?.let {
            userAddress = it
            binding.apply {
                etNamaJalan.setText(it.namaJalan)
                etKelurahan.setText(it.kelurahan)
                etKecamatan.setText(it.kecamatan)
                etKota.setText(it.kota)
                etProvinsi.setText(it.provinsi)
                etKodePos.setText(it.kodePos)
                etDetailAlamat.setText(it.judulAlamat)
                btnHapusAlamat.show()
            }
        }
    }

    private fun setActions() {
        binding.apply {
            btnTambahAlamat.setOnClickListener {
                if (userAddress != null) {
                    viewModel.addAddress(userAddress!!)
                }

                val namaJalan = etNamaJalan.string().trim()
                val kelurahan = etKelurahan.string().trim()
                val kecamatan = etKecamatan.string().trim()
                val kota = etKota.string().trim()
                val provinsi = etProvinsi.string().trim()
                val kodePos = etKodePos.string().trim()
                val judulAlamat = etDetailAlamat.string().trim()

                val address = UserAddress(namaJalan, kelurahan, kecamatan, kota, provinsi, kodePos, judulAlamat)
                viewModel.addAddress(address)
            }

            btnHapusAlamat.setOnClickListener {
                userAddress?.let {
                    viewModel.deleteAddress(it.addressId)
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun observer() {
        viewModel.addNewAddress.observe(this) { result ->
            when (result) {
                is State.Loading -> {
                    binding.btnTambahAlamat.text = ""
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    clearFields()
                    toast("Alamat ${result.data!!.judulAlamat} Berhasil Ditambahkan")
                    finish()
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    clearFields()
                    toast(result.message ?: "Terjadi Kesalahan")
                }

                else -> {}
            }
        }

        viewModel.deleteAddress.observe(this) {
            when (it) {
                is State.Loading -> {
                    binding.btnHapusAlamat.hide()
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    clearFields()
                    toast(it.data.toString())
                    finish()
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    binding.btnHapusAlamat.show()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun clearFields() {
        binding.apply {
            etNamaJalan.setText("")
            etKelurahan.setText("")
            etKecamatan.setText("")
            etKota.setText("")
            etProvinsi.setText("")
            etKodePos.setText("")
            etDetailAlamat.setText("")
            etDetailAlamat.clearFocus()
        }
    }

    private fun checkFields(): Boolean {
        binding.apply {
            return etNamaJalan.string().trim().isNotEmpty() && etDetailAlamat.string().trim()
                .isNotEmpty() &&
                    etKecamatan.string().trim().isNotEmpty() && etKota.string().trim()
                .isNotEmpty() &&
                    etKelurahan.string().trim().isNotEmpty() && etProvinsi.string().trim()
                .isNotEmpty() &&
                    etKodePos.string().trim().isNotEmpty()
        }
    }

    override fun onStart() {
        super.onStart()
        job = lifecycleScope.launch {
            while (true) {
                if (checkFields()) {
                    binding.btnTambahAlamat.isEnabled = true
                }
                delay(1000L)
            }
        }
    }

    override fun onStop() {
        job?.cancel()
        job = null
        super.onStop()
    }

    companion object {
        const val EDIT_ADDRESS = "edit_address"
    }
}