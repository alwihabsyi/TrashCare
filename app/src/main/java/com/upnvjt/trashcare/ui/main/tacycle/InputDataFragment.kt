package com.upnvjt.trashcare.ui.main.tacycle

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.databinding.FragmentInputDataBinding
import java.util.Calendar


class InputDataFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentInputDataBinding
    lateinit var spinner: Spinner

    var day= 0
    var month= 0
    var year= 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInputDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = binding.spinner1

        val valueOfCategory = arrayOf<String>("Organik", "Non Organik", "Limbah B3")
        val arrayAdapter = ArrayAdapter<String>(this.requireContext(), R.layout.style_spinner, valueOfCategory)
        spinner.adapter = arrayAdapter

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.pickDateInput.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this.requireContext(), this, year, month, day)
            datePickerDialog.show()
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = day
        myYear = year
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this.context, this, hour, minute,
            DateFormat.is24HourFormat(this.context))
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        binding.tvPickupDate.text =
            "$myDay - $myMonth - $myYear | $myHour.$myMinute"
    }
}