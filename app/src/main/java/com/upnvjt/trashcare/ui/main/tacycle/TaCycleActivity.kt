package com.upnvjt.trashcare.ui.main.tacycle

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.data.UserAddress
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.databinding.ActivityTaCycleBinding
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

    private lateinit var spinner: Spinner
    private var alamat: UserAddress? = null

    private val viewModel by viewModels<TaCycleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaCycleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSpinner()
        setActions()
        observer()
    }

    private fun observer() {
        viewModel.cycleOrder.observe(this) {
            when (it) {
                is State.Loading -> {
                    binding.tvFindTaRider.hide()
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.tvFindTaRider.show()
                    binding.progressBar.hide()
                    toast("Berhasil order tacycle")
                }
                is State.Error -> {
                    binding.tvFindTaRider.show()
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
    }

    private fun placeOrderDialog() {
        val listSampah: MutableList<String> = mutableListOf()
        listSampah.add(spinner.selectedItem.toString())

        alamat = UserAddress(
            "Jalan Debug",
            "Kelurahan Debug",
            "Kecamatan Debug",
            "Kota Debug",
            "Provinsi Debug"
        )

        val order = TacycleModel(
            TaCycleStatus.OnGoing.cycleStatus,
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRt9IaKw0Aa8WwTuX6h2mc8Jk9ewNmEHCnPaA&usqp=CAU",
            listSampah,
            alamat!!,
            binding.tvPickupDate.text.toString()
        )

        viewModel.placeCycleOrder(order)
    }

    private fun setSpinner() {
        spinner = binding.spinner1

        val valueOfCategory = arrayOf(
            "Plastik",
            "Limbah Dapur",
            "Serbuk Kayu",
            "Kaca",
            "Kertas/Kardus"
        )

        val arrayAdapter = ArrayAdapter(this, R.layout.style_spinner, valueOfCategory)
        spinner.adapter = arrayAdapter
    }

    @SuppressLint("SetTextI18n")
    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvPickupDate.text = dateFormat.format(calendar.time)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TIME_PICKER_ONCE_TAG = "TimePicker"
    }

}