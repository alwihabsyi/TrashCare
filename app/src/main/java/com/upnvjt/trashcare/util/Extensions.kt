package com.upnvjt.trashcare.util

import android.app.Activity
import android.app.Dialog
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.upnvjt.trashcare.R
import com.upnvjt.trashcare.ui.auth.AuthViewPagerAdapter
import java.text.NumberFormat
import java.util.Currency
import kotlin.math.roundToInt


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun ImageView.glide(url: String) {
    Glide.with(this).load(url).into(this)
}

fun Double.toPrice():String{
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("IDR")

    return format.format(this.roundToInt())
}

fun Fragment.setUpForgotPasswordDialog(
    onSendClick: (String) -> Unit
){
    val dialog = Dialog(requireContext(), android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
//    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val etEmail = view.findViewById<EditText>(R.id.etEmailReset)
    val btnCancel = view.findViewById<Button>(R.id.btnCancelReset)
    val btnSend = view.findViewById<Button>(R.id.btnReset)

    btnSend.setOnClickListener {
        val email = etEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}

fun setUpTabLayout(tabLayout: TabLayout, viewPager: ViewPager2, adapter: AuthViewPagerAdapter) {
    tabLayout.addTab(tabLayout.newTab().setText("Login"))
    tabLayout.addTab(tabLayout.newTab().setText("Sign Up"))
    viewPager.adapter = adapter
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab != null) {
                viewPager.currentItem = tab.position
            }

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabReselected(tab: TabLayout.Tab?) {

        }
    })

    val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            tabLayout.selectTab(tabLayout.getTabAt(position))
        }
    }
    viewPager.registerOnPageChangeCallback(myPageChangeCallback)
}

fun EditText.string(): String{
    return text.toString()
}

fun validateEmail(email: String): Validation{
    if(email.isEmpty()){
        return Validation.Failed("Email tidak boleh kosong")
    }

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return Validation.Failed("Harap isi dengan email yang valid")
    }

    return Validation.Success
}

fun validatePassword(password: String, passwordConfirmation: String?): Validation{
    if(password.isEmpty()){
        return Validation.Failed("Password tidak boleh kosong")
    }

    if(passwordConfirmation != null) {
        if(password != passwordConfirmation) {
            return Validation.Failed("Password tidak sesuai")
        }
    }

    if(password.length < 6){
        return Validation.Failed("Password harus terdiri dari 6 huruf atau lebih")
    }

    return Validation.Success
}