package com.upnvjt.trashcare.ui.main.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjt.trashcare.data.user.AddressAdapter
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.databinding.ActivityAddressBinding
import com.upnvjt.trashcare.ui.main.profile.viewmodel.AddressViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private var _binding: ActivityAddressBinding? = null
    private val binding get() = _binding!!
    private val addressAdapter by lazy { AddressAdapter() }
    private val viewModel by viewModels<AddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRv()
        setActions()
        observer()
    }

    private fun setActions() {
        binding.btnTambahAlamat.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }
    }

    private fun observer() {
        viewModel.address.observe(this) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    it.data?.let { address ->
                        setUpRvData(address)
                    }
                }
                is State.Error -> {
                    binding.progressBar.hide()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun setUpRvData(data: List<UserAddress>) {
        addressAdapter.differ.submitList(data)

        addressAdapter.onClick = {
            val intent = Intent(this, AddAddressActivity::class.java)
            intent.putExtra(AddAddressActivity.EDIT_ADDRESS, it)
            startActivity(intent)
        }
    }

    private fun setUpRv() {
        binding.rvAddress.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(this@AddressActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}