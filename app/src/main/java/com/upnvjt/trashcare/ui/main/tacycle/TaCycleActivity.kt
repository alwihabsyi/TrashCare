package com.upnvjt.trashcare.ui.main.tacycle

import android.R
import android.R.attr
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class TaCycleActivity : AppCompatActivity() {

    lateinit var fragmentInputData: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.upnvjt.trashcare.R.layout.activity_ta_cycle)


        fragmentInputData = InputDataFragment()
        loadFragment(fragmentInputData)

    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(com.upnvjt.trashcare.R.id.fragmentContainerTaCycle, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}