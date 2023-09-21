package com.upnvjt.trashcare.ui.main.tacycle

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.databinding.ActivityTaCycleBinding
import com.upnvjt.trashcare.ui.main.profile.AddressActivity
import com.upnvjt.trashcare.ui.main.profile.AddressActivity.Companion.ADDRESS_PICKED
import com.upnvjt.trashcare.ui.main.profile.AddressActivity.Companion.PICK_ADDRESS
import com.upnvjt.trashcare.ui.main.tacycle.cart.TaCycleCartActivity
import com.upnvjt.trashcare.ui.main.tacycle.cart.TaCycleCartActivity.Companion.FROM_ORDER
import com.upnvjt.trashcare.ui.main.tacycle.viewmodel.TaCycleViewModel
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.hide
import com.upnvjt.trashcare.util.show
import com.upnvjt.trashcare.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TaCycleActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private var _binding: ActivityTaCycleBinding? = null
    private val binding get() = _binding!!
    private var alamat: UserAddress? = null
    private val viewModel by viewModels<TaCycleViewModel>()
    private val jenisSampah: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaCycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpPage()
        setActions()
        setCheckBox()
        observer()
    }

    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    private fun setUpPage() {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ADDRESS_PICKED, UserAddress::class.java)
        } else {
            intent.getParcelableExtra(ADDRESS_PICKED)
        }

        if (data != null) {
            alamat = data
            binding.apply {
                tvPilihAlamat.hide()
                linearAlamat.show()

                tvAlamatUser.text = data.judulAlamat
                tvKodePos.text = "ID ${data.kodePos}"
                tvDetailAlamat.text =
                    "${data.namaJalan}, ${data.kelurahan}, ${data.kecamatan}, ${data.kota}, ${data.provinsi}"
            }
        }
    }

    private fun observer() {
        viewModel.cycleOrder.observe(this) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.show()
                }

                is State.Success -> {
                    binding.progressBar.hide()
                    toast("Berhasil order tacycle")
                    val intent = Intent(this, TaCycleCartActivity::class.java)
                    intent.putExtra(FROM_ORDER, true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

                is State.Error -> {
                    binding.progressBar.hide()
                    toast(it.message.toString())
                }
            }
        }
    }

    private fun setActions() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.pickDateInput.setOnClickListener {
            if (alamat == null && jenisSampah.isEmpty()) {
                toast("Harap isi alamat dan jenis sampah")
                return@setOnClickListener
            }

            val timePickerFragmentOne = TimePickerFragment()
            timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
        }

        binding.btnFindRider.setOnClickListener {
            if (binding.tvPickupDate.text.isEmpty()) {
                toast("Harap isi semua muanya")
                return@setOnClickListener
            } else {
                placeOrderDialog()
            }
        }

        binding.layoutAlamat.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra(PICK_ADDRESS, true)
            startActivity(intent)
            finish()
        }
    }

    private fun setCheckBox() {
        binding.apply {
            val checkBoxes = listOf<CheckBox>(
                cbPlastik,
                cbKaca,
                cbLimbahDapur,
                cbKertasKardus,
                cbElektronik,
                cbSerbukKayu
            )

            checkBoxes.forEach {
                it.setOnClickListener { view ->
                    if (view.isActivated){
                        jenisSampah.remove(it.text.toString())
                    } else {
                        jenisSampah.add(it.text.toString())
                    }
                }
            }
        }
    }


    private fun placeOrderDialog() {
        val order = TacycleModel(
            TaCycleStatus.OnGoing.cycleStatus,
            jenisSampah,
            alamat!!,
            binding.tvPickupDate.text.toString()
        )

        viewModel.placeCycleOrder(order)
    }

    @SuppressLint("SetTextI18n")
    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvPickupDate.text = dateFormat.format(calendar.time)
        binding.btnFindRider.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TIME_PICKER_ONCE_TAG = "TimePicker"
    }

}